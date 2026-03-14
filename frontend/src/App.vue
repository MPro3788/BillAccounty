<template>
  <div class="app">
    <!-- Header -->
    <header class="app-header">
      <div class="header-inner">
        <div class="logo">
          <svg viewBox="0 0 32 32" fill="none">
            <rect x="2" y="8" width="12" height="18" rx="2" fill="#1b6ca8" opacity="0.9"/>
            <rect x="18" y="8" width="12" height="18" rx="2" fill="#1e8449" opacity="0.9"/>
            <rect x="8" y="3" width="16" height="4" rx="2" fill="#94a3b8"/>
          </svg>
          <span class="logo-text">BillAccounty <span class="logo-tag">HGB</span></span>
        </div>
        <div class="header-actions" v-if="store.bilanz">
          <span class="dateiname">📄 {{ store.dateiname }}</span>
          <button class="btn-reset" @click="store.reset()">Neue Datei</button>
        </div>
      </div>
    </header>

    <main class="app-main">
      <!-- Upload Phase -->
      <transition name="fade" mode="out-in">
        <div v-if="!store.bilanz && !store.loading" key="upload" class="upload-phase">
          <div class="hero">
            <h1 class="hero-title">Bilanz-Analyse<br/><span class="hero-accent">nach HGB §266</span></h1>
            <p class="hero-sub">Excel oder PDF hochladen — automatische Kategorisierung und Visualisierung Ihrer Bilanzpositionen</p>
          </div>
          <UploadZone />
          <div class="features">
            <div class="feature">
              <span class="feat-icon">⚡</span>
              <span>Automatische Erkennung von Kontonummern (SKR03/SKR04)</span>
            </div>
            <div class="feature">
              <span class="feat-icon">📊</span>
              <span>Aktiva & Passiva nach HGB §266 sortiert</span>
            </div>
            <div class="feature">
              <span class="feat-icon">✓</span>
              <span>Bilanzgleichungsprüfung in Echtzeit</span>
            </div>
          </div>
        </div>

        <!-- Loading Phase -->
        <div v-else-if="store.loading" key="loading" class="loading-phase">
          <div class="loading-inner">
            <div class="big-spinner"></div>
            <p>Datei wird analysiert und kategorisiert...</p>
          </div>
        </div>

        <!-- Bilanz Phase -->
        <div v-else key="bilanz" class="bilanz-phase">
          <!-- Status -->
          <StatusBar
            :summe-aktiva="Number(store.bilanz.summeAktiva)"
            :summe-passiva="Number(store.bilanz.summePassiva)"
            :bilanziert="store.bilanz.bilanziert"
          />

          <!-- Charts -->
          <BilanzChart v-if="store.chartDaten" :daten="store.chartDaten" />

          <!-- Bilanz Tabellen -->
          <div class="bilanz-grid">
            <BilanzSeite
              seite="AKTIVA"
              :positionen="store.gefilterteAktiva"
              :summe="store.bilanz.summeAktiva"
              :kategorien="store.aktivaKategorien"
              :aktueller-filter="store.aktivaFilter"
              @filter="val => store.aktivaFilter = val"
            />
            <BilanzSeite
              seite="PASSIVA"
              :positionen="store.gefiltertePassiva"
              :summe="store.bilanz.summePassiva"
              :kategorien="store.passivaKategorien"
              :aktueller-filter="store.passivaFilter"
              @filter="val => store.passivaFilter = val"
            />
          </div>
        </div>
      </transition>
    </main>

    <footer class="app-footer">
      <span>BilanzTool © {{ new Date().getFullYear() }} · HGB §266 · SKR03/SKR04</span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useBilanzStore } from './stores/bilanzStore'
import UploadZone from './components/UploadZone.vue'
import BilanzSeite from './components/BilanzSeite.vue'
import BilanzChart from './components/BilanzChart.vue'
import StatusBar from './components/StatusBar.vue'

const store = useBilanzStore()
</script>

<style>
/* ===== CSS VARIABLES & RESET ===== */
:root {
  --bg: #0d1117;
  --surface: #161b22;
  --surface-alt: #1c2128;
  --surface-deep: #0d1117;
  --surface-hover: #21262d;
  --border: #30363d;
  --border-light: #21262d;
  --text: #e6edf3;
  --text-muted: #7d8590;
  --accent: #10b981;
  --accent-blue: #1b6ca8;
  --accent-green: #1e8449;
  --radius: 12px;
  --font: 'DM Sans', 'Segoe UI', system-ui, sans-serif;
}

*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

body {
  font-family: var(--font);
  background: var(--bg);
  color: var(--text);
  min-height: 100vh;
  line-height: 1.6;
}

/* ===== LAYOUT ===== */
.app { display: flex; flex-direction: column; min-height: 100vh; }

.app-header {
  position: sticky; top: 0; z-index: 100;
  background: rgba(13,17,23,0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border);
}
.header-inner {
  max-width: 1400px; margin: 0 auto;
  padding: 1rem 2rem;
  display: flex; justify-content: space-between; align-items: center;
}
.logo { display: flex; align-items: center; gap: 0.75rem; }
.logo svg { width: 32px; height: 32px; }
.logo-text { font-size: 1.15rem; font-weight: 700; letter-spacing: -0.02em; }
.logo-tag {
  display: inline-block; font-size: 0.65rem;
  background: var(--accent); color: #000;
  padding: 0.1rem 0.4rem; border-radius: 4px;
  font-weight: 800; letter-spacing: 0.05em; margin-left: 4px; vertical-align: middle;
}

.header-actions { display: flex; align-items: center; gap: 1rem; }
.dateiname { font-size: 0.85rem; color: var(--text-muted); }
.btn-reset {
  padding: 0.45rem 1rem; border-radius: 8px;
  border: 1px solid var(--border);
  background: transparent; color: var(--text);
  font-size: 0.85rem; cursor: pointer; transition: all 0.15s;
  font-family: var(--font);
}
.btn-reset:hover { background: var(--surface-hover); border-color: var(--accent); color: var(--accent); }

.app-main {
  flex: 1;
  max-width: 1400px; margin: 0 auto;
  width: 100%; padding: 2rem;
}

/* ===== UPLOAD PHASE ===== */
.upload-phase { max-width: 640px; margin: 0 auto; display: flex; flex-direction: column; gap: 2rem; }
.hero { text-align: center; }
.hero-title { font-size: 2.5rem; font-weight: 800; line-height: 1.2; letter-spacing: -0.03em; }
.hero-accent { color: var(--accent); }
.hero-sub { margin-top: 0.75rem; color: var(--text-muted); font-size: 1rem; }

.features { display: flex; flex-direction: column; gap: 0.6rem; }
.feature {
  display: flex; align-items: center; gap: 0.75rem;
  padding: 0.7rem 1rem; border-radius: 10px;
  background: var(--surface); border: 1px solid var(--border);
  font-size: 0.875rem; color: var(--text-muted);
}
.feat-icon { font-size: 1rem; }

/* ===== LOADING ===== */
.loading-phase {
  display: flex; justify-content: center; align-items: center;
  min-height: 400px;
}
.loading-inner { display: flex; flex-direction: column; align-items: center; gap: 1.5rem; }
.big-spinner {
  width: 56px; height: 56px;
  border: 4px solid var(--border);
  border-top-color: var(--accent);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.loading-inner p { color: var(--text-muted); font-size: 1rem; }

/* ===== BILANZ PHASE ===== */
.bilanz-phase { display: flex; flex-direction: column; gap: 1.5rem; }
.bilanz-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;
}

/* ===== TRANSITIONS ===== */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-enter-from { opacity: 0; transform: translateY(16px); }
.fade-leave-to { opacity: 0; transform: translateY(-8px); }

/* ===== FOOTER ===== */
.app-footer {
  text-align: center; padding: 1.5rem;
  border-top: 1px solid var(--border);
  color: var(--text-muted); font-size: 0.8rem;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 900px) {
  .bilanz-grid { grid-template-columns: 1fr; }
  .hero-title { font-size: 1.8rem; }
  .app-main { padding: 1rem; }
}
</style>

