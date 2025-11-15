
# Ovládání aplikace:

## myš:

- **levé tlačítko myši**
  - první stisk/tažení vytváří pružnou úsečku (první dva body polygonu)
  - dalní stisk/tažení přidává další vrchol polygonu s pružnou hranou od posledního a prvního bodu
  - klik v blízkosti prvního vrcholu uzavře polygon _(tolerance 10 px)_

- **pravé tlačítko myši**
  - tažením přesun vrcholu polygonu nebo ClipperPolygonu _(tolerance na chycení vrcholu je 10 px)_
  - kliknutí do prostoru spustí SeedFill z daného bodu _(pokud neni zapnuto vyplňování ScanLine)_

- **kolečko myši**
  - při kliku na/v blízkosti úsečky/hrany vytvoří nový vrchol uloženého polygonu _(tolerance 10 px)_

## klávesnice:

 - **SHIFT**
   - pružná úsečka tažená levým tlačítkem myši se chytá na horizontální, vertikální nebo diagonální směr

 - **ENTER**
   - uzavře aktuální polygon

 - **X**
    - maže poslední přidaný vrchol neukončeného polygonu

 - **C**
    - maže celé plátno, všechny objekty a filly

 - **V**
   - přeruší aktuální tažení úsečky/vrcholu levým tlačítkem myši

 - **B**
   - NEimplementováno - změna barevnosti úsečky

 - **D** 
   - zapíná režim kreslení obdelníku (tažením levého tlačítka myši vytvoří základnu a dalším klikem určí výšku obdelníku)
   - pokud je režim kreslení obdelníku vypnut, zruší se aktuální kreslení obdelníku

 - **F**
   - přepíná mezi vyplňováním polygonu metodou ScanLine a SeedFill

 - **G** 
    - vytvoří na pozici kurzoru ClipperPolygon (pokud je kurzor mimo plátno, vytvoří se na středu plátna)
    - vrcholy ClipperPolygonu lze přesouvat pravým tlačítkem myši

 - **H** 
   - otáčí orientaci ořezu na vnitřní a vnější