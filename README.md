## Listen Picture

### Install
```
gradle getDeps
cp src/com/listen_picture/Config.java.example src/com/listen_picture/Config.java
```

Нужно заполнить CLIENT_SECRET в Config классе

### Usage:
```
<bin> -m <mode> <file_path>
```

###Аргументы
1. -m , --mode - по сути выбираем действие, которое будет выполнять программа <br/>
    Доступные значения
    * `play` - генерирование музыки по картинке
    * `encode` - преобразование муз файла в картинку
    * `decode` - проигрывание картинки полученной с помощью `encode`
    
### Examples
```
bin -m play sample/image.png

bin -m encode sample/Help.pm3

bin -m decode sample/encoded.png
```    

### todo
* Сейчас `-m decode` не работает, надо дописать чтобы изображения проигрывались <br/>
Далее нужно генерировать картинку в более интересном виде (пластинка или амплитудный график, пластинка круче) 



