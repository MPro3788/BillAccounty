<template>
  <div class="bilanz-seite">
    <div class="seite-header" :class="seite === 'AKTIVA' ? 'aktiva-header' : 'passiva-header'">
      <h2>{{ seite === 'AKTIVA' ? 'A K T I V A' : 'P A S S I V A' }}</h2>
      <span class="summe-badge">{{ formatBetrag(summe) }}</span>
    </div>

    <!-- Filter -->
    <div class="filter-row">
      <button
        class="filter-btn"
        :class="{ active: !aktuellerFilter }"
        @click="$emit('filter', null)">
        Alle
      </button>
      <button
        v-for="kat in kategorien"
        :key="kat"
        class="filter-btn"
        :class="{ active: aktuellerFilter === kat }"
        @click="$emit('filter', kat)">
        {{ kat }}
      </button>
    </div>

    <!-- Tabelle nach Kategorien gruppiert -->
    <div class="kategorien-liste">
      <div v-for="(gruppe, kat) in gruppiertePosi" :key="kat" class="kategorie-gruppe">
        <div class="kategorie-header" @click="toggleGruppe(kat)">
          <div class="kat-left">
            <span class="chevron" :class="{ open: offeneGruppen.has(kat) }">›</span>
            <span class="kat-name">{{ kat }}</span>
          </div>
          <span class="kat-summe">{{ formatBetrag(gruppenSumme(gruppe)) }}</span>
        </div>

        <transition name="slide">
          <div v-if="offeneGruppen.has(kat)" class="positionen-liste">
            <!-- Unterkategorien -->
            <div v-for="(unterGruppe, unterKat) in gruppiereNachUnter(gruppe)" :key="unterKat">
              <div class="unterkat-header">
                <span class="unterkat-name">{{ unterKat }}</span>
                <span class="unterkat-summe">{{ formatBetrag(gruppenSumme(unterGruppe)) }}</span>
              </div>
              <div v-for="pos in unterGruppe" :key="pos.kontonummer + pos.bezeichnung" class="position-zeile">
                <span class="konto-nr">{{ pos.kontonummer || '—' }}</span>
                <span class="bez">{{ pos.bezeichnung }}</span>
                <span class="betrag">{{ formatBetrag(pos.betrag) }}</span>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <!-- Gesamtsumme -->
    <div class="gesamt-summe">
      <span>Summe {{ seite }}</span>
      <span>{{ formatBetrag(summe) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps({
  positionen: Array,
  summe: Object,
  seite: String,
  kategorien: Array,
  aktuellerFilter: String
})

defineEmits(['filter'])

const offeneGruppen = ref(new Set())

function toggleGruppe(kat) {
  if (offeneGruppen.value.has(kat)) {
    offeneGruppen.value.delete(kat)
  } else {
    offeneGruppen.value.add(kat)
  }
}

const gruppiertePosi = computed(() => {
  const result = {}
  for (const pos of (props.positionen || [])) {
    if (!result[pos.kategorie]) result[pos.kategorie] = []
    result[pos.kategorie].push(pos)
  }
  return result
})

function gruppiereNachUnter(positionen) {
  const result = {}
  for (const pos of positionen) {
    const key = pos.unterkategorie || 'Sonstige'
    if (!result[key]) result[key] = []
    result[key].push(pos)
  }
  return result
}

function gruppenSumme(gruppe) {
  return gruppe.reduce((sum, p) => sum + (Number(p.betrag) || 0), 0)
}

function formatBetrag(val) {
  const num = Number(val) || 0
  return new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2
  }).format(num)
}
</script>

<style scoped>
.bilanz-seite { display: flex; flex-direction: column; gap: 0; }

.seite-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 1.25rem 1.5rem;
  border-radius: 12px 12px 0 0;
}
.aktiva-header { background: linear-gradient(135deg, #0f4c75, #1b6ca8); }
.passiva-header { background: linear-gradient(135deg, #1a3a1a, #2d6a2d); }
.seite-header h2 {
  color: white; font-size: 0.95rem; letter-spacing: 0.3em;
  font-weight: 700; margin: 0;
}
.summe-badge {
  background: rgba(255,255,255,0.2);
  color: white; padding: 0.35rem 0.85rem;
  border-radius: 20px; font-weight: 700; font-size: 0.9rem;
}

.filter-row {
  display: flex; flex-wrap: wrap; gap: 0.4rem;
  padding: 0.75rem 1rem;
  background: var(--surface-alt);
  border-left: 1px solid var(--border);
  border-right: 1px solid var(--border);
}
.filter-btn {
  padding: 0.25rem 0.75rem; border-radius: 20px;
  border: 1px solid var(--border);
  background: transparent; color: var(--text-muted);
  font-size: 0.78rem; cursor: pointer; transition: all 0.15s;
}
.filter-btn.active, .filter-btn:hover {
  background: var(--accent); color: white; border-color: var(--accent);
}

.kategorien-liste {
  border: 1px solid var(--border);
  border-top: none;
}

.kategorie-gruppe { border-bottom: 1px solid var(--border-light); }
.kategorie-gruppe:last-child { border-bottom: none; }

.kategorie-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0.9rem 1.25rem;
  cursor: pointer;
  background: var(--surface);
  transition: background 0.15s;
}
.kategorie-header:hover { background: var(--surface-hover); }
.kat-left { display: flex; align-items: center; gap: 0.6rem; }
.chevron { font-size: 1.2rem; color: var(--accent); transition: transform 0.2s; display: inline-block; }
.chevron.open { transform: rotate(90deg); }
.kat-name { font-weight: 600; font-size: 0.9rem; color: var(--text); }
.kat-summe { font-weight: 700; font-size: 0.9rem; color: var(--text); font-variant-numeric: tabular-nums; }

.positionen-liste { background: var(--surface-deep); }

.unterkat-header {
  display: flex; justify-content: space-between;
  padding: 0.55rem 1.5rem 0.3rem;
  border-top: 1px solid var(--border-light);
}
.unterkat-name { font-size: 0.78rem; color: var(--text-muted); font-style: italic; }
.unterkat-summe { font-size: 0.78rem; color: var(--text-muted); font-variant-numeric: tabular-nums; }

.position-zeile {
  display: grid; grid-template-columns: 70px 1fr auto;
  gap: 0.5rem; align-items: center;
  padding: 0.45rem 1.5rem;
  border-bottom: 1px solid var(--border-light);
  transition: background 0.1s;
}
.position-zeile:hover { background: var(--surface-hover); }
.konto-nr { font-size: 0.75rem; color: var(--text-muted); font-family: monospace; }
.bez { font-size: 0.85rem; color: var(--text); }
.betrag { font-size: 0.85rem; font-weight: 600; color: var(--text); text-align: right; font-variant-numeric: tabular-nums; white-space: nowrap; }

.gesamt-summe {
  display: flex; justify-content: space-between;
  padding: 1rem 1.5rem;
  background: var(--surface-alt);
  border: 1px solid var(--border);
  border-top: 2px solid var(--accent);
  border-radius: 0 0 12px 12px;
  font-weight: 700; font-size: 1rem;
  color: var(--text);
}

.slide-enter-active, .slide-leave-active { transition: all 0.2s ease; overflow: hidden; }
.slide-enter-from, .slide-leave-to { max-height: 0; opacity: 0; }
.slide-enter-to, .slide-leave-from { max-height: 2000px; opacity: 1; }
</style>

