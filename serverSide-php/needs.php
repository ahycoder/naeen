<?php
//header('Content-Type: text/html; charset=utf-8');
date_default_timezone_set("Asia/Tehran");

require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');


$key =$_REQUEST['Key'];
if($key=="Needs"){
    $Filter_ID =$_REQUEST['Filter_ID'];
    $Filter_CITY =$_REQUEST['Filter_CITY'];
    $Filter_FAST =$_REQUEST['Filter_FAST'];
    $Filter_IMAGE =$_REQUEST['Filter_IMAGE'];
    $queryCity="";
    $queryFast="";
    $queryImage="";
    $queryId="";
    if($Filter_ID =="YES"){
        $needscat_id =$_REQUEST['needscat_id'];
        $queryId = " AND needscat_id LIKE '$needscat_id'";
    }else{
        $queryId="";
    }

    if($Filter_CITY =="NO"){
        $queryCity="";
    }else{
        $queryCity=" AND needs_city LIKE '$Filter_CITY'";
    }

    if($Filter_FAST =="YES"){
        $queryFast=" AND needs_fast LIKE '1'";
    }else{
        $queryFast="";
    }

    if($Filter_IMAGE =="YES"){
        $queryImage=" AND needs_imageUrl NOT LIKE '[]'";
    }else{
        $queryImage="";
    }

    $db = new Db();
    $result = $db->query("SELECT * FROM needs WHERE needs_isShow LIKE '1'".$queryId.$queryCity.$queryFast.$queryImage);
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

if($key=="NeedsNoFilter"){
    $db = new Db();
    $result = $db->query("SELECT * FROM needs WHERE needs_isShow LIKE '1'");
    $db->close();
    echo json_encode($result);
}
if($key=="NeedsCat"){
    $db = new Db();
    $result = $db->query("SELECT * FROM needscat");
    $db->close();
    $finalArray=array();
    $finalArray[0]="No";
    $finalArray[1]=$result;
    echo json_encode($finalArray);

}
if($key=="NeedsSearch"){
    $Search =$_REQUEST['Search'];
    $db = new Db();
    $result = $db->query("SELECT * FROM needs WHERE needs_title LIKE '%$Search%' ");
    $db->close();
    echo json_encode($result);
}

if($key=="AddNeeds"){
    $login_userId =$_REQUEST['login_userId'];
    $needscat_id =$_REQUEST['needscat_id'];
    $needs_title =$_REQUEST['needs_title'];
    $needs_desc =$_REQUEST['needs_desc'];
    $needs_city =$_REQUEST['needs_city'];
    $needs_year =$_REQUEST['needs_year'];
    $needs_price =$_REQUEST['needs_price'];
    $needs_area =$_REQUEST['needs_area'];
    $needs_rent =$_REQUEST['needs_rent'];
    $needs_mileage =$_REQUEST['needs_mileage'];
    $needs_kind =$_REQUEST['needs_kind'];
    $needs_room =$_REQUEST['needs_room'];
    $needs_imageUrl =$_REQUEST['needs_imageUrl'];
    $needs_date=date("Y-m-d H:i:s");
    $db = new Db();
    $result = $db->insert("INSERT INTO needs (login_userId,needscat_id,needs_title,needs_desc,needs_city,needs_year,needs_price,needs_area,needs_rent,needs_mileage,needs_kind,needs_room,needs_imageUrl,needs_date) VALUES
       (:login_userId,:needscat_id,:needs_title,:needs_desc,:needs_city,:needs_year,:needs_price,:needs_area,:needs_rent,:needs_mileage,:needs_kind,:needs_room,:needs_imageUrl,:needs_date)", array(
        'login_userId' => $login_userId,
        'needscat_id' => $needscat_id,
        'needs_title' => $needs_title,
        'needs_desc' => $needs_desc,
        'needs_city' => $needs_city,
        'needs_year' => $needs_year,
        'needs_price' => $needs_price,
        'needs_area' => $needs_area,
        'needs_rent' => $needs_rent,
        'needs_mileage' => $needs_mileage,
        'needs_kind' => $needs_kind,
        'needs_room' => $needs_room,
        'needs_imageUrl' => $needs_imageUrl,
        'needs_date' => $needs_date

    ));
    $db->close();
    $finalArray=array();
    if($result){
        $finalArray[0]=200;
        $finalArray[1]="آگهی شما با موفقیت ارسال شد";
    }else{
        $finalArray[0]=400;
        $finalArray[1]="متاسفانه آگهی شما ثبت نشد مجددا تلاش کنید";
    }
    echo json_encode($finalArray);
}
if($key=="NeedsOne"){
    $needs_id =$_REQUEST['needs_id'];
    $db = new Db();
    $result = $db->query("SELECT * FROM needs WHERE needs_id LIKE $needs_id ");
    $db->close();
    echo json_encode($result);
}
if($key=="MyNeeds"){
    $login_userId =$_REQUEST['login_userId'];
    $db = new Db();
    $result = $db->query("SELECT * FROM needs WHERE login_userId LIKE (:login_userId)",array(
        'login_userId' => $login_userId
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

?>