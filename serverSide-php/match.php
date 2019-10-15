<?php
//header('Content-Type: text/html; charset=utf-8');
date_default_timezone_set("Asia/Tehran");

require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
$key =$_POST['Key'];

if($key=="GetList"){
    $db = new Db();
    $match_kind =$_POST['Match_kind'];
    $resultPartMatch = $db->query("SELECT * FROM ccoderi1_town.partmatch ");
    switch ($match_kind){
        case  'MatchPhotography':$match_kind=0;  $match_part=$resultPartMatch[0]['partMatch_Photography'];break;
        case  'MatchReadBook':   $match_kind=1;  $match_part=$resultPartMatch[0]['partMatch_ReadBook'];break;
        case  'MatchPublic':     $match_kind=2;  $match_part=$resultPartMatch[0]['partMatch_Public'];break;
    }
    $result = $db->query("SELECT ccoderi1_town.match.match_id,ccoderi1_town.match.match_imageUrl,ccoderi1_town.match.match_point, ccoderi1_town.login.login_userName,ccoderi1_town.login.login_imgUserUrl
 FROM ccoderi1_town.match LEFT JOIN ccoderi1_town.login ON ccoderi1_town.match.login_userId=ccoderi1_town.login.login_userId WHERE match_kind LIKE (:match_kind) AND match_part LIKE (:match_part) AND match_isShow LIKE (:match_isShow) ORDER BY match_point DESC" ,array(
        'match_kind' => $match_kind,
        'match_isShow' => 1,
        'match_part' => $match_part
    ));
    $db->close();
    $finalArray=array();
    if($result ==null){
        //no find item
        $finalArray[0]="No";
    }else{
        $finalArray=$result;
    }
    echo json_encode($finalArray);

}
if($key=="IsTakePartAlready"){
    $match_part=$_POST['match_part'];
    $login_userId =$_POST['login_userId'];
    $match_kind =$_POST['match_kind'];
    switch ($match_kind){
        case  'MatchPhotography':$match_kind=0;break;
        case  'MatchReadBook':   $match_kind=1; break;
        case  'MatchPublic':     $match_kind=2; break;
    }
    $resultTakePart=array();
    $db = new Db();
    $result = $db->query("SELECT * FROM ccoderi1_town.match WHERE match_kind LIKE (:match_kind) AND login_userId LIKE (:login_userId) AND match_part LIKE (:match_part)", array(
        'login_userId' => $login_userId,
        'match_part' => $match_part,
        'match_kind' => $match_kind
    ));
    if($result !=null){
        $resultTakePart[0]=200;
    }else{
        $resultTakePart[0]=400;
        $resultTakePart[1]='شما قبلا شرکت کرده اید';
    }
    $db->close();

    echo json_encode($resultTakePart);

}

if($key=="takePartMatchPhotograhy"){
    $login_userId =$_POST['login_userId'];
    $match_kind =$_POST['match_kind'];
    $match_imageUrl =$_POST['matchPhoto_imageUrl'];
    $match_part =$_POST['match_part'];
    switch ($match_kind){
        case  'MatchPhotography':$match_kind=0; break;
        case  'MatchReadBook':   $match_kind=1; break;
        case  'MatchPublic':     $match_kind=2; break;
    }
    $db = new Db();
    $result = $db->insert("INSERT INTO ccoderi1_town.match (match_kind,login_userId,match_part,match_imageUrl) VALUES
       (:match_kind,:login_userId,:match_part,:match_imageUrl)", array(
        'match_kind' => $match_kind,
        'login_userId' => $login_userId,
        'match_part' => $match_part,
        'match_imageUrl' => $match_imageUrl
    ));
    $db->close();
    $finalArray=array();
    if($result){
        $finalArray[0]="عکس شما با موفقیت ارسال شد";
    }else{
        $finalArray[0]="متاسفانه عکس شما ثبت نشد مجددا تلاش کنید";
    }
    echo json_encode($finalArray);

}
if($key=="CurrentPart"){
    $result=array();
    $result[0]['MatchPhotography']=0;
    $result[0]['MatchReadBook']=1;
    $result[0]['MatchPublic']=2;
    echo json_encode($result);

}
if($key=="Question"){
    $match_kind =$_POST['match_kind'];
    $match_part =$_POST['match_part'];
    switch ($match_kind){
        case  'MatchReadBook':   $match_kind=1; break;
        case  'MatchPublic':     $match_kind=2; break;
    }
    $db = new Db();
    $result = $db->query("SELECT * FROM ccoderi1_town.question WHERE match_kind LIKE (:match_kind) AND match_part LIKE (:match_part)", array(
        'match_part' => $match_part,
        'match_kind' => $match_kind
    ));
    $db->close();
    echo json_encode($result);


}
if($key=="takePartMatchQuestion"){
    $match_part=$_POST['match_part'];
    $login_userId =$_POST['login_userId'];
    $match_kind =$_POST['match_kind'];
    $correctQuestion =$_POST['correctQuestion'];
    $inCorrectQuestion =$_POST['inCorrectQuestion'];
    $match_point=($correctQuestion*3)-$inCorrectQuestion;
    if($match_point<0){
        $match_point=0;
    }
    switch ($match_kind){
        case  'MatchReadBook':   $match_kind=1; break;
        case  'MatchPublic':     $match_kind=2; break;
    }
    $db = new Db();
    $result = $db->insert("INSERT INTO ccoderi1_town.match (match_kind,login_userId,match_part,match_point) VALUES
       (:match_kind,:login_userId,:match_part,:match_point)", array(
        'match_kind' => $match_kind,
        'login_userId' => $login_userId,
        'match_part' => $match_part,
        'match_point' => $match_point
    ));
    $db->close();
    $finalArray=array();
    if($result){
        $finalArray[0]="مسابقه شما با موفقیت ارسال شد";
    }else{
        $finalArray[0]="متاسفانه مسابقه شما ثبت نشد مجددا تلاش کنید";
    }
    echo json_encode($finalArray);

}
if($key=="AddLikeMatchPhotographyImage"){
    $match_id =$_POST['match_id'];
    $db = new Db();
    $result = $db->modify("UPDATE ccoderi1_town.match SET  `match_point`=(`match_point`+1) WHERE match_id LIKE (:match_id)", array(
        'match_id' => $match_id
    ));
    $db->close();
}
if($key=="RemoveLikeMatchPhotographyImage"){
    $match_id =$_POST['match_id'];
    $db = new Db();
    $result = $db->modify("UPDATE ccoderi1_town.match SET  `match_point`=(`match_point`-1) WHERE match_id LIKE (:match_id)", array(
        'match_id' => $match_id
    ));
    $db->close();
}