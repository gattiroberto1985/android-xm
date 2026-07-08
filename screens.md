# Screen - appoggio

## Home page

Di massima: 

	┌─────────────────────────────────────┐
	│ [≡] Settings  Search  [◀ 30gg ▶]   │
	├─────────────────────────────────────┤
	│         Bilancio: €1,234.56         │
	├─────────────────────────────────────┤
	│                                     │
	│        [Pie Chart da dati]          │
	│                                     │
	├─────────────────────────────────────┤
	│ ▼ Legenda (Espandibile)             │
	│   🟦 Food       €234.56   (19%)     │
	│   🟦 Transport  €156.78   (12%)     │
	│   🟦 Other      €843.22   (68%)     │
	└─────────────────────────────────────┘

Il menu offre le voci:

- Settings
- Search

Stati possibili:

- iniziale, da caricare
- finale, dato caricato ok
- intermedio, in caricamento
- da ricaricare causa filtri cambiati dall'utente
- finale, con ko

Eventi che l'utente triggera:

- `onDateRangeChanged` --> invalida dati e ricarica
- `onCategoryTap` --> apre nuovo screen
- `onLegendToggle` --> comprime/espande la legenda
- `onSettingsTap` --> apre nuovo screen con le preferenze utente dell'app
- `onSearchTap` --> apre nuovo screen per ricerca transazioni
- `onRetry` --> triggerato quando si scorre dall'alto verso il basso con il dito