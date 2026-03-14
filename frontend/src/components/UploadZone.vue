<template>
  <div
    class="upload-zone"
    @dragover.prevent
    @dragenter.prevent="dragging = true"
    @dragleave.prevent="dragging = false"
    @drop.prevent="onDrop"
  >
    <input
      ref="fileInput"
      type="file"
      class="hidden-input"
      accept=".xlsx,.xls,.csv,.pdf"
      @change="onFileChange"
    />
    <div class="upload-inner" :class="{ dragging }" @click="openFileDialog">
      <div class="icon">⬆</div>
      <h2>Bilanzdatei hochladen</h2>
      <p>Ziehe eine Excel- oder PDF-Datei hierher oder klicke zum Auswählen.</p>
      <p class="hint">Unterstützt: Excel (.xlsx, .xls, .csv) und PDF</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useBilanzStore } from '../stores/bilanzStore'

const dragging = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)
const store = useBilanzStore()

function openFileDialog() {
  fileInput.value?.click()
}

function handleFile(file: File | null | undefined) {
  if (!file) return
  store.ladeBilanz(file)
}

function onFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  handleFile(file)
}

function onDrop(event: DragEvent) {
  dragging.value = false
  const file = event.dataTransfer?.files?.[0]
  handleFile(file)
}
</script>

<style scoped>
.upload-zone {
  border-radius: 16px;
  border: 1px dashed var(--border);
  padding: 1px;
  background: radial-gradient(circle at top left, rgba(16,185,129,0.25), transparent 55%);
}
.upload-inner {
  border-radius: 15px;
  background: var(--surface);
  padding: 2.5rem 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}
.upload-inner.dragging {
  border-color: var(--accent);
  background: var(--surface-hover);
  transform: translateY(-1px);
}
.icon {
  font-size: 2rem;
  margin-bottom: 0.75rem;
}
h2 {
  font-size: 1.4rem;
  margin-bottom: 0.5rem;
}
p {
  color: var(--text-muted);
  font-size: 0.9rem;
}
.hint {
  margin-top: 0.75rem;
  font-size: 0.8rem;
}
.hidden-input {
  display: none;
}
</style>

