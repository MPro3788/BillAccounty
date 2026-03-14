<template>
  <div class="chart-card">
    <h3>Übersicht Aktiva / Passiva</h3>
    <p class="hint">
      Hier könnte eine Chart-Library (z.B. Chart.js oder ECharts) integriert werden.
      Aktuell wird nur eine einfache Zusammenfassung angezeigt.
    </p>
    <div class="summary">
      <div class="col">
        <h4>Aktiva</h4>
        <ul>
          <li v-for="p in daten.aktiva" :key="p.bezeichnung + p.kontonummer">
            <span class="name">{{ p.bezeichnung }}</span>
            <span class="value">{{ format(p.betrag) }}</span>
          </li>
        </ul>
      </div>
      <div class="col">
        <h4>Passiva</h4>
        <ul>
          <li v-for="p in daten.passiva" :key="p.bezeichnung + p.kontonummer">
            <span class="name">{{ p.bezeichnung }}</span>
            <span class="value">{{ format(p.betrag) }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  daten: {
    aktiva: { bezeichnung: string; kontonummer?: string; betrag: number }[]
    passiva: { bezeichnung: string; kontonummer?: string; betrag: number }[]
  }
}>()

function format(val: number) {
  return new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR',
    minimumFractionDigits: 2
  }).format(Number(val) || 0)
}
</script>

<style scoped>
.chart-card {
  border-radius: 12px;
  border: 1px solid var(--border);
  background: var(--surface);
  padding: 1.25rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
h3 {
  font-size: 1rem;
}
.hint {
  font-size: 0.8rem;
  color: var(--text-muted);
}
.summary {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}
.col h4 {
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}
ul {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 220px;
  overflow: auto;
}
li {
  display: flex;
  justify-content: space-between;
  gap: 0.5rem;
  font-size: 0.8rem;
  padding: 0.2rem 0;
}
.name {
  color: var(--text-muted);
}
.value {
  font-variant-numeric: tabular-nums;
}
@media (max-width: 900px) {
  .summary {
    grid-template-columns: 1fr;
  }
}
</style>

