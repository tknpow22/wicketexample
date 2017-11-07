# wicketexample

Apache Wicket 8 Example Application

## 作成には eclipse Neon.3 Release (4.6.3): http://mergedoc.osdn.jp/ を利用させていただきました。
  ありがたく感謝いたします。どうもありがとうございます。

## データベースは SQLite の In-Memory Databases 機能を使用しています。
  そのため、アプリケーション起動時に毎回初期化(データベースの作成等)を行っています。
    → ExampleApplication#createAppDatabase()

## データベースのコネクション引き回しの良い方法が浮かばず、
  使う際に、その時その時でコネクションを取得するようにしています。
  マッパーや永続フレームワークを使えば、その辺りはおまかせにできるのですが、
  このサンプルでは、とりあえず、素の JDBC を利用しています。

## 使用しているフレームワーク、ライブラリは以下のとおりです。

    - Apache Wicket 8.0.0-M8
        https://wicket.apache.org/

    - Apache Commons
        https://commons.apache.org/

    - Open JSON
        https://github.com/openjson/openjson

    - SLF4J
        https://www.slf4j.org/

    - Serializable java.util.function Interfaces
        https://github.com/danekja/jdk-serializable-functional

    - Apache Log4j 2
        https://logging.apache.org/log4j/2.x/

    - Jackson
        https://github.com/FasterXML/jackson

    - Google Core Libraries for Java
        https://github.com/google/guava

    - sqlite-jdbc
        https://bitbucket.org/xerial/sqlite-jdbc

