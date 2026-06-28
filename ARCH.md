# Architettura

## Modello dati

- Categorie di spesa (id, nome, colore)
- Tipologia di spesa (entrata, uscita)
- Movimento finanziario (id, tipologia, importo, data, descrizione, categoria)
- ...?


## Use cases di alto livello


### Categorie di spesa

Deve essere presente sempre la categoria "Other", default per tutte le transazioni
quando non specificato diversamente dall'utente.

- ricerca categorie di spesa:
	- byId
 - byName (nome parziale / match "intelligente")
- Inserimento categoria di spesa
- Aggiornamento categoria di spesa
- Cancellazione categoria di spesa (con spostamento su categoria "Other")


### Transazioni

- inserimento transazione
- modifica transazione
- rimozione transazione
- search transazioni:
	- byId
 - byCategory (match esatto)
 - byDate (da / entro / tra)
 - byDescription (match intelligente di stringa)


### Export / Import

- esportazione spese in JSON / CSV
- esportazione stato completo in JSON / CSV
- import spese da JSON/CSV
- import stato completo da JSON / CSV


### Aggregazioni

L'app presenterà diverse possibilità di aggregazione dei dati. La home page
a scorrimento mostrerà:
- pagina di riepilogo con saldo netto totale sul periodo prescelto
- lista delle ultime transazioni nel periodo prescelto	
- dovrà consentire di aggregare le transazioni per categoria

altre da definire

## Todo

- Movimenti ricorrenti