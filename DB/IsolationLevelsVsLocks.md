# __Возможно, пользователь путает блокировки (locks) с уровнями изоляции (isolation levels), поэтому стоит разграничить эти понятия. Уровни изоляции определяют степень изоляции транзакций, а блокировки — это механизмы, которые СУБД использует для реализации этих уровней.__

___

# 📌 Уровни изоляции, блокировки PostgreSQL и Hibernate: как они связаны

## 1. Уровни изоляции vs Блокировки: Базовая концепция

- **Уровни изоляции** (Isolation Levels):
    - Определяют, *какие аномалии* допускаются между параллельными транзакциями.
    - Регламентированы стандартом SQL (ANSI): `READ UNCOMMITTED`, `READ COMMITTED`, `REPEATABLE READ`, `SERIALIZABLE`.

- **Блокировки** (Locks):
    - *Механизмы реализации* уровней изоляции на уровне СУБД (например, PostgreSQL).
    - Примеры: `FOR UPDATE`, `FOR SHARE`, Advisory Locks.

---

## 2. Уровни изоляции в PostgreSQL

### Поддерживаемые уровни:
| Уровень              | Грязное чтение | Неповторяемое чтение | Фантомы |
|----------------------|----------------|-----------------------|---------|
| **READ UNCOMMITTED** | ❌             | ❌                    | ❌      |
| **READ COMMITTED**   | ✅             | ❌                    | ❌      |
| **REPEATABLE READ**  | ✅             | ✅                    | ❌      |
| **SERIALIZABLE**     | ✅             | ✅                    | ✅      |

> ⚠️ В PostgreSQL `READ UNCOMMITTED` работает как `READ COMMITTED` ([документация](https://www.postgresql.org/docs/current/transaction-iso.html)).

---

## 3. Как Hibernate использует уровни изоляции

### Настройка через аннотацию `@Transactional`:
```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void updateOrder(Long orderId) {
// ... логика
}
```

**Доступные варианты** (из `org.springframework.transaction.annotation.Isolation`):
- `DEFAULT` → Уровень по умолчанию СУБД (для PostgreSQL: `READ COMMITTED`)
- `READ_UNCOMMITTED`
- `READ_COMMITTED`
- `REPEATABLE_READ`
- `SERIALIZABLE`

---

## 4. Оптимистичные блокировки в Hibernate

### Реализация через `@Version`:
```java
@Entity
public class Order {
@Id
private Long id;

    @Version
    private Integer version;
    // ... другие поля
}
```

**Как работает**:
1. При чтении сущности Hibernate сохраняет версию.
2. При обновлении добавляет условие `WHERE version = :oldVersion`.
3. Если версия изменилась → выбрасывает `OptimisticLockException`.

**Связь с уровнями изоляции**:
- Требует как минимум `READ COMMITTED` (чтобы видеть актуальные версии).
- Не использует пессимистичные блокировки на уровне БД.

---

## 5. Пессимистичные блокировки в Hibernate

### Использование `LockModeType`:
```java
Order order = entityManager.find(
Order.class,
1L,
LockModeType.PESSIMISTIC_WRITE
);
```

**Генерируемый SQL**:
```sql
SELECT * FROM orders WHERE id = 1 FOR UPDATE;
```

**Типы блокировок**:
| Режим                | SQL                  | Описание                     |
|----------------------|----------------------|------------------------------|
| `PESSIMISTIC_READ`   | `FOR SHARE`          | Блокировка на чтение         |
| `PESSIMISTIC_WRITE`  | `FOR UPDATE`         | Эксклюзивная блокировка      |
| `PESSIMISTIC_FORCE_INCREMENT` | `FOR UPDATE` + версия | Блокировка + обновление версии |

---

## 6. Как выбрать стратегию?

### Когда использовать **оптимистичные** блокировки:
- Низкая конкуренция за данные.
- Долгие транзакции (например, UI-формы).
- Распределенные системы.

### Когда использовать **пессимистичные** блокировки:
- Высокая конкуренция за одни данные.
- Короткие транзакции.
- Критически важные операции (например, списание денег).

---

## 7. Пример: Комбинирование подходов

```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void processOrder(Long orderId) {
// Пессимистичная блокировка на чтение
Order order = entityManager.find(
Order.class,
orderId,
LockModeType.PESSIMISTIC_WRITE
);

    // Оптимистичная проверка через версию
    if (order.getStatus() == Status.NEW) {
        order.setStatus(Status.PROCESSING);
    }
    
    entityManager.flush(); // Проверка версии произойдет здесь
}
```

---

## 8. Ограничения PostgreSQL

- **Нет "грязного чтения"** → `READ UNCOMMITTED` не имеет смысла.
- **REPEATABLE READ vs SERIALIZABLE**:
    - В PostgreSQL `REPEATABLE READ` предотвращает фантомы через *snapshot isolation*.
    - `SERIALIZABLE` использует *predicate locking* (более строго, но дороже).

---

## 9. Советы по настройке Hibernate

1. **Для оптимистичных блокировок**:
```properties
# application.properties
spring.jpa.properties.hibernate.connection.isolation=2 # READ_COMMITTED
```

2. **Для пессимистичных блокировок**:
   ```java
   @QueryHints({
   @QueryHint(
   name = "javax.persistence.lock.timeout",
   value = "5000" // 5 сек ожидания
   )
   })
   @Query("SELECT o FROM Order o WHERE o.id = :id")
   Order findWithLock(@Param("id") Long id);
   ```

3. **Мониторинг**:
   ```sql
   SELECT * FROM pg_locks WHERE pid = [ваш PID];
   ```

---

## 🔥 Итог

- **Уровни изоляции** → Определяют *что* разрешено (аномалии).
- **Блокировки** → Реализуют *как* достигается изоляция.
- **Hibernate** → Предоставляет абстракцию над:
    - Уровнями изоляции СУБД (`@Transactional`).
    - Оптимистичными (`@Version`) и пессимистичными (`LockModeType`) блокировками.

Выбор стратегии зависит от сценария: комбинируйте подходы для баланса между производительностью и надежностью!