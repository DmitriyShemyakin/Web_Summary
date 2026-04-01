package com.example.web_summaryy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Контроллер для UI страниц (Thymeleaf)
 */
@Controller
public class ViewController {

    /**
     * Главная страница - редирект на список аварий
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/incidents";
    }

    /**
     * Страница логина
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Список аварий
     */
    @GetMapping("/incidents")
    public String incidents() {
        return "incidents/list";
    }

    /**
     * Редактирование открытой аварии
     */
    @GetMapping("/incidents/{id}/edit")
    public String editIncident(@PathVariable("id") Long id, Model model) {
        model.addAttribute("incidentId", id);
        return "incidents/edit";
    }

    /**
     * Управление сменами
     */
    @GetMapping("/shifts")
    public String shifts() {
        return "shifts/index";
    }

    /**
     * Аварии выбранной смены (открытые и закрытые, привязанные к смене по shift_id)
     */
    @GetMapping("/shifts/{id}/incidents")
    public String shiftIncidents(@PathVariable("id") Long shiftId, Model model) {
        model.addAttribute("shiftId", shiftId);
        return "shifts/incidents";
    }

    /**
     * Админка - управление пользователями
     */
    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    /**
     * Админка - управление ролями
     */
    @GetMapping("/admin/roles")
    public String adminRoles() {
        return "admin/roles";
    }

    /**
     * Админка - управление справочниками
     */
    @GetMapping("/admin/dictionaries")
    public String adminDictionaries() {
        return "admin/dictionaries";
    }
}






