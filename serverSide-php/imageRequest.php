<?php
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
require_once(getcwd() . '/town.php');
$domain="http://ccoder.ir/";
if($_SERVER['REQUEST_METHOD']=='POST'){
    if($_POST['Key']=="userImage"){
        $image = $_POST['image'];
        $name = $_POST['name'];
        $path = "image/user/$name.png";
        $actualpath = $domain.$path;
        file_put_contents($path,base64_decode($image));
        echo $actualpath;

    }
    if($_POST['Key']=="NeedImage"){
        $image = $_POST['image'];
        $name = $_POST['name'];
        $date=date("Y_m_d_H_i_s");
        $path = "image/needs/$date$name.png";
        $actualpath = $domain.$path;
        file_put_contents($path,base64_decode($image));
        echo $actualpath;
    }
    if($_POST['Key']=="MathPhotographyImage"){
        $image = $_POST['image'];
        $name = $_POST['name'];
        $date=date("Y_m_d_H_i_s");
        $path = "image/match/photography/$date$name.png";
        $actualpath = $domain.$path;
        file_put_contents($path,base64_decode($image));
        echo $actualpath;
    }
    if($_POST['Key']=="ChangeUserImage"){
        $imageBeforeUrl = $_POST['imageBeforeUrl'];
        $userId = $_POST['UserId'];
        $file=str_replace("http://ccoder.ir","/home/ccoderi1/public_html",$imageBeforeUrl);
        if (file_exists($file)) {
            unlink($file);
        }
        $image = $_POST['imageNew'];
        $name = $_POST['name'];
        $path = "image/user/$name.png";
        $actualpath = $domain.$path;
        file_put_contents($path,base64_decode($image));
        $db = new Db();
        $result = $db->insert("UPDATE login SET login_imgUserUrl=:login_imgUserUrl WHERE login_userId LIKE :login_userId", array(
            'login_imgUserUrl' => $actualpath,
            'login_userId' => $userId
        ));
        $db->close();
        echo $actualpath;
    }

}
else
{
  echo "Error";
}
?>