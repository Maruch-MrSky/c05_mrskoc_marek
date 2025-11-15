# PGRF1.2025.c05

Projekt pro cvičení ÚT 14:55 - 16:30.

## Úloha 2: Vyplnění a ořezání n-úhelníkové oblasti

Navažte na předchozí úlohu implementující zadávání a vykreslování vyplněného polygonu (n-úhelníku).

1. Implementujte algoritmus **semínkového vyplnění** rastrově zadané oblasti.
   - Myší zadanou hranici oblasti vykreslete na rastrovou plochu plátna barvou odlišnou od barvy vyplnění.
   - ***Stisknutím tlačítka myši*** Kliknutím vyberte počáteční pixel záplavového algoritmu a plochu vybarvěte.
   - Uvažujte **dvě možnosti** hraniční podmínky vyplňování. Jednak **omezení barvou pozadí** a jednak **barvou hranice**. Viz prezentace *Kurz PGRF1 Oliva -> Oznámení -> Cvičení Šteflovič -> Prezentace c05*.

2. Implementujte funkci pro **kreslení obdelníka** zadaného *základnou* a třetím bodem jehož vzdálenost od základny určuje jeho *výšku*.
   - Zadání základny obdelníka: stisk plus tažení myši.
   - Následně zadání výšky obdelníka: výběr bodu kliknutím v prostoru.
   - Pro uložení vytvořte speciální třídu dědící z třídy *Polygon*.

3. Implementujte algoritmus **ořezání** libovolného uzavřeného n-úhelníku konvexním polygonem.
   - Ořezávací polygon může být fixně zadán a MUSÍ mít alespoň **pět vrcholů**. Oba útvary, ořezávaný i ořezávací, jsou zadány polygonem tvořících jejich obvod (geometricky zadaná hranice).
   - Ořezávací polygon uvažujte pouze jako konvexní, případně s kladnou i zápornou orientací vrcholů.

4. Implementujte Scan-line algoritmus vyplnění geometricky zadané plochy n-úhelníku, který je výsledkem ořezání v předchozím kroku.

5. Implementujte funkci na klávesu ```C``` pro mazání plátna a všech datových struktur.

6. Nutno mít vytvořený GITový repozitář s výsledným kódem.

***K implementaci použijte rozhraní a třídy definované na cvičeních. Třídy případně upravte nebo doplňte o potřebné metody.***

Návrhy pro tuto úlohy naleznete v modulu *task2* (viz *Kurz PGRF1 Oliva -> Obsah -> Ukázky a návody*). Nebo v hlavní větvi tohoto repozitáře. Tyto ukázky aplikačního řešení nejsou dogma, můžete je modifikovat nebo navrhnout vlastní.

### Bonusy
#### Bonus 1
Doplňte možnost editace již zadaného n-úhelníku, změna pozice vrcholu, případně smazání stávajícího či přidání nového vrcholu.

#### Bonus 2
Při vyplňovaní **rastrově** i **vektorově** zadané hranice implementujte také variantu vyplnění útvaru pravidelně se opakujícím vzorem zadaným předpisem v rozhraní **PatternFill**.

#### Bonus 3
Implementace Seed Fill algoritmu pomocí **fronty** či **zásobníku**.

#### Bonus 4
Pravidelný a vhodně okomentovaný commit do GITového repozitáře.

### Hodnocení

Při hodnocení je kladen důraz na **funkčnost** programu pro libovolně zadané koncové body, na **přesnost** vykreslení a na kvalitu návrhu a čitelnost kódu. Kód vhodně rozdělte do rozhraní a tříd. **Kód očistěte** od ladicích či pokusných nefunkčních částí.

### Termín odevzdání
Do pátku **14.11.2025, 23:59**.
Odevzdávejte prostřednictvím BB (Olivy), před odevzdáním si znovu přečtěte pravidla odevzdávání semestrálních projektů a průběžných úloh (Viz níže).
Odevzdání úlohy si prosím nenechávejte na poslední chvíli!

### Pravidla odevzdání úloh

Projekty odevzdávejte prostřednictvím Blackboard (BB - Oliva):

    Kurz PGRF1 -> Úkoly -> Úloha 2 (klikněte na název úkolu)

- Veškeré soubory související s prací odevzdávejte zabalené v archivu ve formátu **zip** (případně rar, 7z, …).
    - Soubor pojmenujte ve tvaru **cviceni_prijmeni_jmeno.zip** bez ***diakritiky*** (např. c03_novak_vaclav.zip případně kf_novak_vaclav.zip).

    - Pokud nevíte číslo cvičení, které navštěvujete, podívejte se do [rozvrhu](https://fim.uhk.cz/rozvrhy/ttable.asp?identifier=KIKM%2FPGRF1&weeks=1-13&idtype=name&objectclass=module&periods=2-17&width=100).

Soubor bude obsahovat adresářovou strukturu. Příklad:

```
c0X_novak_vaclav.zip
│   
└───c0X_novak_vaclav
    │   README.md
    │
    └── src
    │   └─   ZDROJOVÉ
    │   └─    SOUBORY   
    │   └─   PROGRAMU
    │
    └── doc
        └─   PozadavkyPGRF1_Task2_2025.docx
```

- Jeden hlavní adresář pojmenovaný stejně jako soubor archivu (např. c03_novak_vaclav případně kf_novak_vaclav)

- Podadresář src obsahující zdrojové soubory *.java umístěné do adresářů podle definovaných package (struktura projektu v prostředí např. IntelliJ, Eclipse a pod.)

- Případně další podadresáře s přiloženými soubory např. doc, res, ...
- Pokud se vám podaří vygenerovat spustitelnou formu aplikace (jar, případně exe) umístěte ji do hlavního adresáře.

    - V žádném případě **nepřibalujte** adresář **bin** se soubory _*.class_.

    - Pokud aplikace vyžaduje další binární soubory (obrázky, data) ponechejte je tak, aby byly ve stejné relativní cestě vůči hlavnímu adresáři, jako je to ve vašem projektu. Tzn. ponechejte celou adresářovou strukturu projektu bez adresáře bin.

Před odevzdáním si vyzkoušejte rozbalení archivu a funkčnost odevzdávaného projektu.

- V případě složitějšího členění zdrojových kódů musí být z názvu jasné, která třída je aplikační.

- **Zdrojový kód** bude _logicky členěn, formátován_ a _strukturován_ a bude v rozumné míře _komentován_ s vhodně volenými názvy tříd, proměnných, metod atd. Je velmi doporučeno _použít funkci přeformátování_ zdrojového souboru.

- Případné komentáře či poznámky k implementaci uložte do textových souborů v hlavním adresáři (notes.txt, todo.txt, readme.md)

- Pokud je součástí zadání **vyplnění auto-evaluační tabulky** (tabulka požadavků), vyplňte splnění požadované funkcionality společně se způsobem ovládání a případně doplňte vámi doplněné funkce nad rámec zadání. Tabulku nahrajte do hlavního adresáře a zabalte do výsledného archivu.

- Na soubory umístěné mimo adresář (např. **c03_novak_vaclav**) **NEBUDE** brán zřetel.

- **Nepište poznámky** do formuláře pro odevzdání úloh v BB.