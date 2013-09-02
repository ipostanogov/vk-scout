vk-scout
========

Последнюю версию можно скачать [здесь](http://goo.gl/9jAiIS).

Возможности
--------------
* Всплывающие оповещения о новых сообщениях ВКонтакте
* При щелчке на оповещении [ЛКМ](http://ru.wikipedia.org/wiki/Щелчок_%28нажатие_клавиши%29#.D0.9B.D0.B5.D0.B2.D0.B0.D1.8F_.D0.BA.D0.BD.D0.BE.D0.BF.D0.BA.D0.B0) - переход к диалогу, при [ПКМ](http://ru.wikipedia.org/wiki/Щелчок_%28нажатие_клавиши%29#.D0.9F.D1.80.D0.B0.D0.B2.D0.B0.D1.8F_.D0.BA.D0.BD.D0.BE.D0.BF.D0.BA.D0.B0) - отметить прочитанным
* Поддержка смайликов

Настройки
------------
* Возможность сохранения логина и пароля в [зашифрованном](http://ru.wikipedia.org/wiki/RSA) виде
* Поддержка как встроенного ключа шифрования, как и уникального 
* Возможность задания параметра запуска браузера (для тех, у кого их несколько)

#### Создание своего ключа 
1. Выйти из программы
2. Установить openssl (также есть в папке с git)
3. Выполнить скрипт
 
        set RANDFILE=.rnd
        openssl genrsa -out vkey.pem 2048
        openssl pkcs8 -topk8 -nocrypt -in vkey.pem -inform PEM -out vkey.private.der -outform DER
        openssl rsa -in vkey.pem -pubout -outform DER -out vkey.public.der
        rm .rnd
        rm vkey.pem
4. В домашней папке ([`%userprofile%`](http://ru.wikipedia.org/wiki/Переменная_среды#.D0.9D.D0.B5.D0.BA.D0.BE.D1.82.D0.BE.D1.80.D1.8B.D0.B5_.D0.BF.D0.B5.D1.80.D0.B5.D0.BC.D0.B5.D0.BD.D0.BD.D1.8B.D0.B5_.D1.81.D1.80.D0.B5.D0.B4.D1.8B) под Windows) открыть .vk-scout и заменить его следующим содержанием

        {
            "RSAKeysFileNames" : {
                "public" : "ваш\\путь\\до\\vkey.public.der",
                "private" : "ваш\\путь\\до\\vkey.private.der"
            }
        }
Двойные [обратные слеши](http://ru.wikipedia.org/wiki/Обратная_косая_черта) обязательны!!!
5. Запустить программу снова

#### Задание параметров запуска браузера 
Открыть .vk-scout в домашней папке и привести к похожему виду

    {
        "WebBrowser" : [
            "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe",
            "-P",
            "myprivateprofile",
            "-new-tab"
        ]
    }

#### Задание нескольких настроек 
Файл .vk-scout в домашней папке следует привести к виду

    {
        "RSAKeysFileNames" : {
            ...        
        },
        "WebBrowser" : [
            ...
        ]
    }


Сборка из исходников
------------------------
* убедиться, что в пути java `System.getProperty(“user.home”)` все символы латинские;
* скачать и установить [sbt](http://www.scala-sbt.org/0.12.2/docs/Getting-Started/Setup.html), [jdk](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html);
* зайти в папку с исходниками из консоли;
* выполнить последовательно sbt, update, assembly;
* забрать результат в `\target\scala-2.10\vk-scout-assembly-*.jar`


История изменений
------------------------- 
#### v0.1.0.1 
* Уменьшено потребление оперативной памяти путём принудительного запуска сборщика мусора
* Добавлен автоматический перезапуск программы при старте, если задан слишком большой (и ненужный) объём кучи

#### v0.1 
* Первая публичная версия
