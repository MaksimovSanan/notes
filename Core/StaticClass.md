# Разница между `private static` и `private` вложенными классами в Java

## 1. `private` вложенный класс (обычный нестатический внутренний класс)
- Связан с экземпляром внешнего класса.
- Может напрямую обращаться к нестатическим полям и методам внешнего класса.
- Для создания объекта такого класса нужно сначала создать объект внешнего класса.

### Пример:
```java
public class OuterClass {
    private int instanceField = 42;

    private class InnerClass {
        public int getOuterField() {
            return instanceField; // Доступ к нестатическому полю внешнего класса
        }
    }

    public InnerClass createInner() {
        return new InnerClass();
    }
}

public class Main {
    public static void main(String[] args) {
        OuterClass outer = new OuterClass();
        OuterClass.InnerClass inner = outer.createInner(); // Через экземпляр внешнего класса
        System.out.println(inner.getOuterField());
    }
}
```

## 2. `private static` вложенный класс (статический внутренний класс)
- Не связан с экземпляром внешнего класса.
- Не имеет доступа к нестатическим полям и методам внешнего класса.
- Может обращаться только к статическим полям и методам внешнего класса.
- Создается независимо от объекта внешнего класса.

### Пример:
```java
public class OuterClass {
    private static int staticField = 100;

    private static class StaticInnerClass {
        public int getOuterStaticField() {
            return staticField; // Доступ только к статическим полям внешнего класса
        }
    }

    public static StaticInnerClass createStaticInner() {
        return new StaticInnerClass();
    }
}

public class Main {
    public static void main(String[] args) {
        OuterClass.StaticInnerClass staticInner = OuterClass.createStaticInner();
        System.out.println(staticInner.getOuterStaticField());
    }
}
```

## Сравнение:
| Характеристика                           | `private` вложенный класс         | `private static` вложенный класс |
|------------------------------------------|------------------------------------|-----------------------------------|
| Связь с экземпляром внешнего класса       | Да                                | Нет                              |
| Доступ к нестатическим членам внешнего класса | Да                                | Нет                              |
| Доступ к статическим членам внешнего класса | Да                                | Да                               |
| Требование экземпляра внешнего класса     | Да                                | Нет                              |

## Когда использовать:
- **`private` вложенный класс**: если класс логически связан с экземпляром внешнего класса и часто использует его нестатические члены.
- **`private static` вложенный класс**: если класс является вспомогательным и не зависит от экземпляра внешнего класса.
