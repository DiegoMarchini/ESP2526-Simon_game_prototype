# Simon Game Prototype

Progetto finale del corso
**Programmazione di Sistemi Embedded 25-26**

## Organizzazione del codice
Le varie classi utilizzate nel progetto sono suddivise in più sottopacchetti:  
**audio** : contiene la classe per la gestione delle tracce audio  
**data** : contiene entità, DAO e Database Room  
**logic** : contiene la classe che funge da "computer di gioco"  
Le 3 activity e i relativi ViewModel si trovano nel package principale

## Sviluppo e testing

**NOTA**: Provando ad emulare su Android Studio il profilo HW del Dispositivo 1 è possibile che la densità dello schermo non coincida con quella indicata qui sotto per via di approssimazioni di Android Studio. In tal caso si può usare il profilo HW del Dispositivo 2.

### 1. Dispositivo Principale (Fisico)

**Tipo dispositivo:** Smartphone Fisico  
**Dispositivo:** Samsung Galaxy A34 5G (SM-A346B)   
**Android version:** 16    
**Android API Level**: 36.0    
**Schermo:** 1080 x 2340 px   
**Densità schermo:** ~390 dpi (categoria: xxhdpi)   
**RAM:** 8 GB   

### 2. Dispositivo Secondario (Virtuale)

**Tipo dispositivo:** Virtuale (Android Virtual Device)   
**Dispositivo:** Google Pixel 2   
**Android version:** 16    
**Android API Level**: 36.0    
**Schermo:** 1080 x 1920 px   
**Densità schermo:** ~420 dpi (categoria: xxhdpi)   
**RAM:** 4 GB 