package helper.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import main.G;

public class HelperDatabase extends SQLiteOpenHelper {

  public static final int DB_VERSION = 2;
  private static HelperDatabase instance;
  public HelperDatabase() {
    super(G.context, G.DB_DIR + "/" + G.DB_NAME, null, DB_VERSION);
  }
  public static HelperDatabase getInstance(){
    if(instance == null){
      synchronized (HelperDatabase.class) {
        if(instance == null){
          instance = new HelperDatabase();
        }
      }
    }
    return instance;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    createImageTable(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion == 1 && newVersion == 2) {
      createImageTable(db);
    }
  }

  private void createImageTable(SQLiteDatabase db) {
    String queryTableBanner =
      "CREATE TABLE 'banners' (" +
        "'banner_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'banner_key' TEXT, " +
        "'banner_url' TEXT, " +
        "'banner_idRelated' INTEGER " +
        ")";

    String queryTablePartMatch =
      "CREATE TABLE 'partMatch' (" +
        "'partMatch_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'partMatch_Photography' INTEGER, " +
        "'partMatch_ReadBook' INTEGER, " +
        "'partMatch_Public' INTEGER " +
        ")";
    String queryTableAnsweable =
      "CREATE TABLE 'answerabls' (" +
        "'answerable_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'answerable_name' TEXT, " +
        "'answerable_title' TEXT, " +
        "'answerable_imageUrl' TEXT, " +
        "'answerable_showComments' INTEGER, " +
        "'answerable_sort' INTEGER " +
        ")";
    String queryTableNeedsCat =
      "CREATE TABLE 'needscat' (" +
        "'needscat_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'needscat_name' TEXT, " +
        "'needscat_up' INTEGER, " +
        "'needscat_isYear' INTEGER, " +
        "'needscat_isPrice' INTEGER, " +
        "'needscat_isArea' INTEGER, " +
        "'needscat_isRent' INTEGER, " +
        "'needscat_isMileage' INTEGER, " +
        "'needscat_isKind' INTEGER, " +
        "'needscat_isRoom' INTEGER " +
        ")";

    String queryTableCity =
      "CREATE TABLE 'city' (" +
        "'city_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'city_name' TEXT, " +
        "'city_sort' INTEGER, " +
        "'city_isShow' INTEGER " +
        ")";
    String queryTableCommentNewsLike =
      "CREATE TABLE 'commentnewslike' (" +
        "'commentnewslike_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'commentnews_id' INTEGER " +
        ")";
    String queryTableCommentAnswerableLike =
      "CREATE TABLE 'commentanswerablelike' (" +
        "'commentanswerablelike_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'commentanswerable_id' INTEGER " +
        ")";
    String queryTableMatchPhotographyLike =
      "CREATE TABLE 'matchphotographylike' (" +
        "'matchphotographylike_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'match_id' INTEGER, " +
        "'match_part' INTEGER " +
        ")";
    String queryTableNewsMarked =
      "CREATE TABLE 'newsmarked' (" +
        "'news_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'news_seen' INTEGER, " +
        "'news_title' TEXT, " +
        "'news_date' TEXT, " +
        "'news_imgUrl' TEXT " +
        ")";
    String queryTableNeedsMarked =
      "CREATE TABLE 'needsmarked' (" +
        "'needs_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'needs_price' INTEGER, " +
        "'needs_title' TEXT, " +
        "'needs_date' TEXT, " +
        "'needs_imageUrl' TEXT " +
        ")";
    String queryTableMyNeeds =
      "CREATE TABLE 'myneeds' (" +
        "'needs_id' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL , " +
        "'needs_price' INTEGER, " +
        "'needs_title' TEXT, " +
        "'needs_date' TEXT, " +
        "'needs_imageUrl' TEXT " +
        ")";
    db.execSQL(queryTableBanner);
    db.execSQL(queryTablePartMatch);
    db.execSQL(queryTableAnsweable);
    db.execSQL(queryTableNeedsCat);
    db.execSQL(queryTableCity);
    db.execSQL(queryTableCommentNewsLike);
    db.execSQL(queryTableCommentAnswerableLike);
    db.execSQL(queryTableMatchPhotographyLike);
    db.execSQL(queryTableNewsMarked);
    db.execSQL(queryTableNeedsMarked);
    db.execSQL(queryTableMyNeeds);
  }
}