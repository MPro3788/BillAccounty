<template>
  <div class="status-bar">
    <div class="left">
      <span class="label">Aktiva</span>
      <span class="value">{{ format(summeAktiva) }}</span>
    </div>
    <div class="center" :class="{ ok: bilanziert, bad: !bilanziert }">
      <span v-if="bilanziert">Bilanz ausgeglichen ✔</span>
      <span v-else>Bilanz nicht ausgeglichen ✖</span>
    </div>
    <div class="right">
      <span class="label">Passiva</span>
      <span class="value">{{ format(summePassiva) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  summeAktiva: number
  summePassiva: number
  bilanziert: boolean
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
.status-bar {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 1.5rem;
  padding: 1rem 1.25rem;
  border-radius: 12px;
  background: var(--surface);
  border: 1px solid var(--border);
}
.left, .right {
  display: flex;
  flex-direction: column;
  gap: 0.1rem;
}
.left .label, .right .label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--text-muted);
}
.value {
  font-size: 1rem;
  font-weight: 700;
}
.center {
  padding: 0.4rem 0.9rem;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 600;
  border: 1px solid transparent;
}
.center.ok {
  background: rgba(16,185,129,0.12);
  border-color: rgba(16,185,129,0.5);
  color: #6ee7b7;
}
.center.bad {
  background: rgba(248,113,113,0.08);
  border-color: rgba(248,113,113,0.5);
  color: #fecaca;
}
</style>

