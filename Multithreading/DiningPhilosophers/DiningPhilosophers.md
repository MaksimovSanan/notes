# Resource monitoring

## Использование jstack
   Если вы запускаете приложение в JVM, вы можете получить дамп состояния потоков с помощью утилиты jstack:

1. Найдите PID процесса Java (например, через jps).
2. Выполните команду:
```bash
jstack <PID>
```
Это покажет состояния всех потоков в JVM.



## Использование jconsole (Java Monitoring and Management Console)
   jconsole — это стандартный инструмент, поставляемый с JDK, который позволяет мониторить состояние JVM, включая потоки:

1. Запустите приложение с включённым JMX (Java Management Extensions):
- ps по-моему необязательно, хз я и так подключился, какой-то варнинг только словил

```bash
java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-jar your-application.jar
```

2. Откройте jconsole:

```bash
jconsole
```

3.Подключитесь к запущенному приложению. На вкладке Threads вы сможете наблюдать состояния потоков в реальном времени.



## Использование VisualVM
   VisualVM — это мощный инструмент для мониторинга приложений Java.

1. Установите VisualVM (он входит в состав JDK или скачивается отдельно).
2. Запустите приложение.
3. Откройте VisualVM, и оно автоматически обнаружит ваше приложение.
4. Перейдите на вкладку Threads, где будет графическое отображение состояний потоков, включая их переходы между состояниями.