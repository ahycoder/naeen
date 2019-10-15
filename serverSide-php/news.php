<?php
//header('Content-Type: text/html; charset=utf-8');
//date_default_timezone_set("Asia/Tehran");

require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
require_once(getcwd() . '/town.php');




$key =$_POST['Key'];
$town = new Town();
//$finalResult=array();
$result=array();

if($key=="News"){
$result=$town->allSelect("news");
echo json_encode($result);
}
//news_id

if($key=="NewsOne"){
    $news_id =$_POST['news_id'];
    $result=$town->newsOne($news_id);
    echo json_encode($result);
}

if($key=="SendCommentOneNews"){
    $text =$_POST['text'];
    $user_id =$_POST['user_id'];
    $news_id =$_POST['news_id'];
    $newsComment_date= date("Y-m-d H:i:s");
    $db = new Db();
    $result = $db->insert("INSERT INTO newscomment (newsComment_text,login_userId,newsComment_date,news_id) VALUES
       (:newsComment_text,:login_userId,:newsComment_date,:news_id)", array(
        'newsComment_text' => $text,
        'login_userId' => $user_id,
        'newsComment_date' => $newsComment_date,
        'news_id' => $news_id
    ));
    $db->close();
    $finalArray=array();
    $finalArray[0]=$result;
    echo json_encode($finalArray);
}
if($key=="ReadCommentsOneNews"){
    $news_id =$_POST['news_id'];
    $db = new Db();
    $finalResult=array();
    $resultComments = $db->query("SELECT * FROM newscomment WHERE news_id LIKE (:news_id) AND newsComment_isOk LIKE (:newsComment_isOk) ORDER BY newsComment_like DESC", array(
        'news_id' => $news_id,
        'newsComment_isOk' => 1
    ));

    $finalArray=array();
    if($resultComments ==null){
        //no find item
        $finalArray[0]="No";
    }else{
        foreach ($resultComments as $result) {
            unset($result['news_id']);
            unset($result['newsComment_violation']);
            unset($result['newsComment_sort']);
            unset($result['newsComment_isShow']);
            $login_userId = $result['login_userId'];
            $resultsImg = $db->query("SELECT login_imgUserUrl FROM login WHERE login_userId LIKE (:login_userId) ", array(
                'login_userId' => $login_userId
            ));
            $result['login_imgUserUrl']=$resultsImg[0]['login_imgUserUrl'];
            $finalResult[]=$result;
        }
        $db->close();
        $finalArray=$finalResult;
    }
    echo json_encode($finalArray);
}
if($key=="AddLikeCommentNews"){
    $newsComment_id =$_POST['newsComment_id'];
    $db = new Db();
    $result = $db->modify("UPDATE newscomment SET  `newsComment_like`=(`newsComment_like`+1) WHERE newsComment_id LIKE (:newsComment_id)", array(
        'newsComment_id' => $newsComment_id
    ));
    $db->close();
}
if($key=="RemoveLikeCommentNews"){
    $newsComment_id =$_POST['newsComment_id'];
    $db = new Db();
    $result = $db->modify("UPDATE newscomment SET  `newsComment_like`=(`newsComment_like`-1) WHERE newsComment_id LIKE (:newsComment_id)", array(
        'newsComment_id' => $newsComment_id
    ));
    $db->close();
}