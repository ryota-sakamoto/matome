# まとめツール (仮)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/da331c3732d249d6b1c0a3989f5ae8b9)](https://www.codacy.com/app/ryota-sakamoto/matome?utm_source=github.com&utm_medium=referral&utm_content=ryota-sakamoto/matome&utm_campaign=badger)


## 開発環境
- Windows10
- Intellij IDEA 2017 Ultimate
- CentOS7

## バージョン
```
$ java -version // 1.8.0_77
$ scala -version // 2.12.3
# elasticsearch --version // 5.4.3
```

## 使用方法
```
$ docker-compose up -d
$ docker exec -it app bash
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