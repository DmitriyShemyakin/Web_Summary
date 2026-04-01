(function (global) {
    'use strict';

    function parseDate(v) {
        if (!v) return null;
        if (Array.isArray(v) && v.length >= 3) {
            return new Date(v[0], v[1] - 1, v[2], v[3] || 0, v[4] || 0, v[5] || 0);
        }
        const d = new Date(v);
        return isNaN(d.getTime()) ? null : d;
    }

    function diffMs(startVal, endVal) {
        const s = parseDate(startVal);
        const e = endVal ? parseDate(endVal) : new Date();
        if (!s || !e) return null;
        const ms = e.getTime() - s.getTime();
        return ms >= 0 ? ms : null;
    }

    function msToHHMM(ms) {
        if (ms == null || ms < 0) return '—';
        const totalMin = Math.floor(ms / 60000);
        const h = Math.floor(totalMin / 60);
        const m = totalMin % 60;
        return String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0');
    }

    global.IncidentTime = { parseDate, diffMs, msToHHMM };
})(typeof window !== 'undefined' ? window : this);
