# 📌 Уровни изоляции транзакций: подробный разбор для собеседования

## 1. Что такое уровни изоляции?

**Уровень изоляции (Isolation Level)** — настройка СУБД, определяющая, как транзакции взаимодействуют друг с другом при параллельном выполнении.  
Цель: баланс между **производительностью** и **консистентностью данных**.

---

## 2. 4 уровня изоляции (ANSI SQL)

### Уровень 1: READ UNCOMMITTED
- **Грязное чтение (Dirty Read):** Да ✅
- **Неповторяемое чтение (Non-Repeatable Read):** Да ✅
- **Фантомы (Phantoms):** Да ✅

**Пример:**  
```sql
-- Транзакция 1
BEGIN;
UPDATE users SET balance = 100 WHERE id = 1; -- Не коммитит

-- Транзакция 2 (READ UNCOMMITTED)
BEGIN;
SELECT balance FROM users WHERE id = 1; -- Увидит 100 (грязные данные)
```

**Особенность PostgreSQL:**  
`READ UNCOMMITTED` работает как `READ COMMITTED` ([документация](https://www.postgresql.org/docs/current/transaction-iso.html)).

---

### Уровень 2: READ COMMITTED (дефолт в PostgreSQL)
- **Грязное чтение:** Нет ❌
- **Неповторяемое чтение:** Да ✅
- **Фантомы:** Да ✅

**Пример:**  
```sql
-- Транзакция 1
BEGIN;
UPDATE users SET balance = 200 WHERE id = 1;
COMMIT;

-- Транзакция 2 (READ COMMITTED)
BEGIN;
SELECT balance FROM users WHERE id = 1; -- Вернет 100 (старые данные)
-- После коммита Транзакции 1:
SELECT balance FROM users WHERE id = 1; -- Вернет 200 (новые данные)
```

**Когда использовать:**
- Больше чтений, чем записей.
- Допустимы неконсистентные данные в рамках транзакции.

---

### Уровень 3: REPEATABLE READ
- **Грязное чтение:** Нет ❌
- **Неповторяемое чтение:** Нет ❌
- **Фантомы:** Да ✅ (но в PostgreSQL — нет! 🚀)

**Пример в PostgreSQL:**  
```sql
-- Транзакция 1 (REPEATABLE READ)
BEGIN;
SELECT * FROM users WHERE age > 30; -- Вернет 10 записей

-- Транзакция 2
INSERT INTO users (age) VALUES (35); COMMIT;

-- Транзакция 1
SELECT * FROM users WHERE age > 30; -- Все равно 10 записей (фантомов нет)
```

**Особенность PostgreSQL:**  
Использует **Snapshot Isolation** → предотвращает фантомы.  
Это **не по стандарту ANSI**, но более надежно.

---

### Уровень 4: SERIALIZABLE
- **Грязное чтение:** Нет ❌
- **Неповторяемое чтение:** Нет ❌
- **Фантомы:** Нет ❌

**Как работает:**  
Транзакции выполняются так, как будто они запущены последовательно.

**Пример:**  
```sql
-- Транзакция 1 (SERIALIZABLE)
BEGIN;
SELECT COUNT(*) FROM users WHERE balance > 1000; -- 5

-- Транзакция 2
INSERT INTO users (balance) VALUES (2000); COMMIT;

-- Транзакция 1
UPDATE users SET status = 'VIP' WHERE balance > 1000;
-- Если SERIALIZABLE: ошибка "could not serialize access"
```

**Когда использовать:**
- Критически важные данные (финансы, аудит).
- Высокий риск конфликтов.

---

## 3. Как выбрать уровень изоляции?

| Уровень              | Производительность | Консистентность | Сценарии использования                |
|----------------------|--------------------|-----------------|---------------------------------------|
| **READ COMMITTED**   | Высокая            | Низкая          | Блог, каталог товаров                 |
| **REPEATABLE READ**  | Средняя            | Средняя         | Отчеты, аналитика                     |
| **SERIALIZABLE**     | Низкая             | Высокая         | Банковские операции, бронирования     |

---

## 4. Уровни изоляции в Hibernate

### Настройка через Spring:
```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void updateUser(Long userId) {
// ...
}
```

**Доступные уровни:**
- `Isolation.DEFAULT` (READ COMMITTED в PostgreSQL)
- `Isolation.READ_COMMITTED`
- `Isolation.REPEATABLE_READ`
- `Isolation.SERIALIZABLE`

---

## 5. Частые вопросы на собеседовании

### Вопрос 1:
**"Почему в PostgreSQL нет грязного чтения (Dirty Read)?"**

**Ответ:**
- Архитектура PostgreSQL основана на **MVCC (Multiversion Concurrency Control)**.
- Данные читаются из **снимка (snapshot)** на момент начала транзакции.
- Реальные данные не блокируются для чтения.

---

### Вопрос 2:
**"Как REPEATABLE READ в PostgreSQL предотвращает фантомы?"**

**Ответ:**
- PostgreSQL использует **Snapshot Isolation** для уровня `REPEATABLE READ`.
- Все операции в транзакции видят данные на момент первого запроса.
- Новые строки, добавленные другими транзакциями, не видны (фантомы блокируются).

---

### Вопрос 3:
**"Когда использовать SERIALIZABLE?"**

**Ответ:**
- Когда **консистентность важнее производительности**.
- Примеры:
    - Списание денег с двух счетов одновременно.
    - Бронирование последнего билета на рейс.

**Предупреждение:**
- Частые ошибки `Serialization Failure` → нужно реализовать повторы (retry logic).

---

## 6. Практические советы

### Для собеседования:
- **Говорите про MVCC:**  
  "PostgreSQL использует MVCC, поэтому даже при REPEATABLE READ не блокирует данные для чтения".
- **Упомяните Snapshot Isolation:**  
  "В отличие от других СУБД, PostgreSQL предотвращает фантомы на уровне REPEATABLE READ".
- **Примеры из практики:**  
  "В нашем проекте мы использовали SERIALIZABLE для финансовых транзакций, но перешли на REPEATABLE READ + оптимистичные блокировки для производительности".

### В коде:
- **Проверяйте ошибки:**  
  ```java
  try {
  someService.updateData();
  } catch (CannotSerializeTransactionException ex) {
  // Retry logic
  }
  ```
- **Мониторинг:**  
  ```sql
  SELECT * FROM pg_stat_activity WHERE state = 'idle in transaction';
  ```

---

## 7. Чеклист для самопроверки

✅ Понимаю разницу между уровнями изоляции.  
✅ Знаю, как PostgreSQL обрабатывает REPEATABLE READ.  
✅ Могу объяснить, почему SERIALIZABLE — это не всегда "золотой стандарт".  
✅ Умею настраивать уровни изоляции в Spring/Hibernate.  
✅ Знаю, как обрабатывать ошибки сериализации.