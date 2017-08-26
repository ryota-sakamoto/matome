# まとめツール (仮)

## 開発環境
- Windows10
- Intellij IDEA 2017 Ultimate

## 言語のバージョン
```
$ java -version // 1.8.0_77
$ scala -version // 2.11
```

## 使用方法 (docker-composeにする予定)
```
$ docker build -t ryota-sakamoto/matome .
$ docker run -itd -p 80:9000 --name matome ryota-sakamoto/matome
$ docker exec -it matome bash
$ activator
```

## 実装

### フロント
- お気に入り記事
- 設定の削除
- 退会
- 管理画面

### バック
- Actorのエラー処理

### そのうち
- リファクタリング
- 絵文字問題
- API
- UI周り (Bootstrap => MUI?)
- Modelの共通化(DIしたい)