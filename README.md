# zde bude ovládání a potřebné informace
## ovládání:

## postřehy:
- ### úloha 2:
    implementace IsHorizontal() v ScanLine rozbijí vykreslování, to samé Orientate() a IsInside(). 
    Problém je, že při orientaci a nasledném isInside() má levá i pravá hrana stejnou orientaci, takže obě mají "vnitřek" na stejnou stranu. 
