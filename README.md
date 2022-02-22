# soundcloud

software requirements

* git
* maven
* java 8

to build the project run

```bash
git clone https://github.com/hawolt/mobafire-bot
cd soundcloud-downloader
bash setup.sh
```

this will create scdl.sh which you can run using

```bash
./scdl.sh
```

when run without arguments it will print a little help message

```
Command          | Shortcut  | Required  | Argument  | Once  | Description
download         | dl        | true      | false     | false | resource(s) to download
threads          | t         | false     | false     | true  | specify amount of threads
directory        | dir       | false     | false     | true  | directory to save files
```

to download something simply use the `-dl` or `--download` arg followed by the soundcloud url, you can use this argument more than once

```bash
./scdl.sh -dl https://soundcloud.com/hawolt/miracle-zevran-bootleg
```

if you want to speed up the downloads or change the default download directory you can use `--directory /path/to/dir` or `--threads 10` but be aware that to many threads will cause rate limit issues which are currently not handled properly
