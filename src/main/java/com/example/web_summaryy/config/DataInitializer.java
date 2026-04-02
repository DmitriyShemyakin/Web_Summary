package com.example.web_summaryy.config;

import com.example.web_summaryy.config.seed.IncidentCategorySeedData;
import com.example.web_summaryy.dto.position.PositionDtoResponse;
import com.example.web_summaryy.model.IncidentCategory;
import com.example.web_summaryy.model.IncidentType;
import com.example.web_summaryy.model.NetworkType;
import com.example.web_summaryy.model.Role;
import com.example.web_summaryy.model.User;
import com.example.web_summaryy.repository.IncidentCategoryRepository;
import com.example.web_summaryy.repository.IncidentTypeRepository;
import com.example.web_summaryy.repository.NetworkTypeRepository;
import com.example.web_summaryy.repository.PositionRepository;
import com.example.web_summaryy.repository.RoleRepository;
import com.example.web_summaryy.repository.UserRepository;
import com.example.web_summaryy.service.UpdateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_ROLE_TITLE = "admin";
    private static final String SEED_FILE = "bs.json";
    /** Единый пароль для тестовых пользователей с ролями дежурств (см. лог при старте). */
    private static final String SEED_DUTY_USER_PASSWORD = "password";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PositionRepository positionRepository;
    private final NetworkTypeRepository networkTypeRepository;
    private final IncidentTypeRepository incidentTypeRepository;
    private final IncidentCategoryRepository incidentCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UpdateService updateService;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) {
        initAdmin();
        seedNetworkTypes();
        seedIncidentTypes();
        seedIncidentCategories();
        seedDutyUsersAndRoles();
        seedPositionsFromFile();
    }

    private void initAdmin() {
        Role adminRole = roleRepository.findByTitle(ADMIN_ROLE_TITLE)
                .orElseGet(() -> {
                    log.info("Роль '{}' не найдена, создаю", ADMIN_ROLE_TITLE);
                    return roleRepository.save(Role.builder().title(ADMIN_ROLE_TITLE).build());
                });

        if (userRepository.existsByUsername(ADMIN_USERNAME)) {
            log.info("Пользователь '{}' уже существует, пропускаю", ADMIN_USERNAME);
            return;
        }

        User admin = User.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder.encode(ADMIN_USERNAME))
                .fullName("Администратор")
                .objRole(Set.of(adminRole))
                .isActive(true)
                .build();

        userRepository.save(admin);
        log.info("Создан пользователь '{}' с ролью '{}'", ADMIN_USERNAME, ADMIN_ROLE_TITLE);
    }

    private void seedNetworkTypes() {
        record Nt(String code, String title) {}
        List<Nt> rows = List.of(
                new Nt("GSM", "GSM"),
                new Nt("LTE", "LTE"),
                new Nt("GSM_LTE", "GSM/LTE")
        );
        for (Nt nt : rows) {
            if (!networkTypeRepository.existsByCode(nt.code())) {
                networkTypeRepository.save(NetworkType.builder().code(nt.code()).title(nt.title()).build());
                log.info("Добавлен тип сети: {} ({})", nt.title(), nt.code());
            }
        }
    }

    private void seedIncidentTypes() {
        List<String> codes = List.of(
                "ТМ", "ЭЛ", "РРЛ", "RBS", "ПР", "ИБП", "ОПС", "МСПД",
                "ДГУ", "ССС", "Аренда", "ВОЛС"
        );
        for (String code : codes) {
            if (!incidentTypeRepository.existsByTypeCode(code)) {
                incidentTypeRepository.save(IncidentType.builder().typeCode(code).isActive(true).build());
                log.info("Добавлен тип аварии: {}", code);
            }
        }
    }

    private void seedIncidentCategories() {
        int added = 0;
        for (String name : IncidentCategorySeedData.categoryNames()) {
            if (!incidentCategoryRepository.existsByCategoryName(name)) {
                incidentCategoryRepository.save(IncidentCategory.builder()
                        .categoryName(name)
                        .isActive(true)
                        .build());
                added++;
            }
        }
        if (added > 0) {
            log.info("Добавлено категорий аварий: {}", added);
        }
    }

    /**
     * У каждого пользователя одна роль с тем же названием, что и title.
     */
    private void seedDutyUsersAndRoles() {
        record DutySeed(String username, String roleTitle) {}
        List<DutySeed> seeds = List.of(
                new DutySeed("kurgan_hmyan", "Дежурный Курган/ХМЯН"),
                new DutySeed("yuzn", "Дежурный ЮЗН"),
                new DutySeed("ntg", "Оператор НТГ"),
                new DutySeed("transport", "Дежурный транспорта"),
                new DutySeed("yadro", "Дежурный ядра")
        );

        boolean anyUserCreated = false;
        for (DutySeed s : seeds) {
            Role role = roleRepository.findByTitle(s.roleTitle())
                    .orElseGet(() -> roleRepository.save(Role.builder().title(s.roleTitle()).build()));

            if (userRepository.existsByUsername(s.username())) {
                continue;
            }

            User user = User.builder()
                    .username(s.username())
                    .password(passwordEncoder.encode(SEED_DUTY_USER_PASSWORD))
                    .fullName(s.roleTitle())
                    .objRole(Set.of(role))
                    .isActive(true)
                    .build();
            userRepository.save(user);
            anyUserCreated = true;
            log.info("Создан пользователь '{}' с ролью '{}'", s.username(), s.roleTitle());
        }

        if (anyUserCreated) {
            log.warn("Пароль для новых тестовых пользователей (дежурных): {}", SEED_DUTY_USER_PASSWORD);
        }
    }

    private void seedPositionsFromFile() {
        if (positionRepository.count() > 0) {
            log.info("Позиции уже есть в БД ({} шт.). Загрузка из файла пропущена.", positionRepository.count());
            return;
        }

        ClassPathResource resource = new ClassPathResource(SEED_FILE);
        if (!resource.exists()) {
            log.info("Файл '{}' не найден в classpath — пропускаю загрузку seed-данных", SEED_FILE);
            return;
        }

        try (InputStream is = resource.getInputStream()) {
            PositionDtoResponse[] data = objectMapper.readValue(is, PositionDtoResponse[].class);
            log.info("Загружено {} позиций из '{}', начинаю импорт в БД...", data.length, SEED_FILE);
            updateService.syncPositionsFromArray(data);
            log.info("Импорт позиций из '{}' завершён", SEED_FILE);
        } catch (Exception e) {
            log.error("Ошибка загрузки seed-данных из '{}': {}", SEED_FILE, e.getMessage(), e);
        }
    }
}
