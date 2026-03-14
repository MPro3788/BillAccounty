import { defineStore } from 'pinia'
import axios from 'axios'

interface BilanzPosition {
  kontonummer?: string
  bezeichnung: string
  betrag: number
  kategorie?: string
  unterkategorie?: string
}

interface BilanzAntwort {
  summeAktiva: number
  summePassiva: number
  bilanziert: boolean
  aktiva: BilanzPosition[]
  passiva: BilanzPosition[]
}

export const useBilanzStore = defineStore('bilanz', {
  state: () => ({
    bilanz: null as BilanzAntwort | null,
    loading: false,
    dateiname: '' as string,
    aktivaFilter: null as string | null,
    passivaFilter: null as string | null
  }),
  getters: {
    gefilterteAktiva(state): BilanzPosition[] {
      if (!state.bilanz) return []
      if (!state.aktivaFilter) return state.bilanz.aktiva
      return state.bilanz.aktiva.filter(p => p.kategorie === state.aktivaFilter)
    },
    gefiltertePassiva(state): BilanzPosition[] {
      if (!state.bilanz) return []
      if (!state.passivaFilter) return state.bilanz.passiva
      return state.bilanz.passiva.filter(p => p.kategorie === state.passivaFilter)
    },
    aktivaKategorien(state): string[] {
      if (!state.bilanz) return []
      return Array.from(new Set(state.bilanz.aktiva.map(p => p.kategorie).filter(Boolean))) as string[]
    },
    passivaKategorien(state): string[] {
      if (!state.bilanz) return []
      return Array.from(new Set(state.bilanz.passiva.map(p => p.kategorie).filter(Boolean))) as string[]
    },
    chartDaten(state) {
      if (!state.bilanz) return null
      return {
        aktiva: state.bilanz.aktiva,
        passiva: state.bilanz.passiva
      }
    }
  },
  actions: {
    async ladeBilanz(datei: File) {
      this.loading = true
      this.dateiname = datei.name
      try {
        const formData = new FormData()
        formData.append('file', datei)

        // Erwarteter Endpoint im Backend: POST /api/bilanz/upload
        const response = await axios.post<BilanzAntwort>('/api/bilanz/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })

        this.bilanz = response.data
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.bilanz = null
      this.loading = false
      this.dateiname = ''
      this.aktivaFilter = null
      this.passivaFilter = null
    }
  }
})

