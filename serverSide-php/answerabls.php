<?php
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
require_once(getcwd() . '/town.php');

$key =$_POST['Key'];
$result=array();
if($key=="Answerabls"){
    $db = new Db();
    $result = $db->query("SELECT * FROM answerabls");
    $db->close();
    echo json_encode($result);
}

if($key=="AnswerableOne"){
    $answerable_id =$_POST['answerable_id'];
    $db = new Db();
    $results = $db->query("SELECT * FROM answerabls WHERE answerable_id LIKE (:answerable_id)", array(
        'answerable_id' => $answerable_id
    ));
    $db->close();
    echo json_encode($results);

}
if($key=="SendCommentToAnswerable"){
    $answerableComment_text =$_POST['answerableComment_text'];
    $login_userId =$_POST['login_userId'];
    $answerable_id =$_POST['answerable_id'];
    $answerableComment_private =$_POST['answerableComment_private'];
    $answerableComment_date= date("Y-m-d H:i:s");
    $db = new Db();
    $result = $db->insert("INSERT INTO answerableComment (answerableComment_text,login_userId,answerableComment_private,answerableComment_date,answerable_id) VALUES
       (:answerableComment_text,:login_userId,:answerableComment_private,:answerableComment_date,:answerable_id)", array(
        'answerableComment_text' => $answerableComment_text,
        'login_userId' => $login_userId,
        'answerableComment_date' => $answerableComment_date,
        'answerableComment_private' => $answerableComment_private,
        'answerable_id' => $answerable_id
    ));
    $db->close();
    $finalArray=array();
    $finalArray[0]=$result;
    echo json_encode($finalArray);
}
if($key=="ReadCommentsOneAnswerable"){
    $answerable_id =$_POST['answerable_id'];
    $db = new Db();
    $finalResult=array();
    $resultComments = $db->query("SELECT * FROM answerablecomment WHERE answerable_id LIKE (:answerable_id) AND answerableComment_isOk LIKE (:answerableComment_isOk) ORDER BY answerableComment_like DESC", array(
        'answerable_id' => $answerable_id,
        'answerableComment_isOk' => 1
    ));
    $finalArray=array();
    if($resultComments ==null){
        //no find item
        $finalArray[0]="No";
    }else{
        foreach ($resultComments as $result) {
            unset($result['answerable_id']);
            unset($result['answerableComment_sort']);
            unset($result['answerableComment_isShow']);
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
if($key=="AddLikeCommentAnswerable"){
    $answerableComment_id =$_POST['answerableComment_id'];
    $db = new Db();
    $result = $db->modify("UPDATE answerablecomment SET  `answerableComment_like`=(`answerableComment_like`+1) WHERE answerableComment_id LIKE (:answerableComment_id)", array(
        'answerableComment_id' => $answerableComment_id
    ));
    $db->close();
}
if($key=="RemoveLikeCommentAnswerable"){
    $answerableComment_id =$_POST['answerableComment_id'];
    $db = new Db();
    $result = $db->modify("UPDATE answerablecomment SET  `answerableComment_like`=(`answerableComment_like`-1) WHERE answerableComment_id LIKE (:answerableComment_id)", array(
        'answerableComment_id' => $answerableComment_id
    ));
    $db->close();
}