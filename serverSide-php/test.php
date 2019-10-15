<?php
date_default_timezone_set("Asia/Tehran");
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');


$db = new Db();
$result = $db->query("SELECT * FROM needscat");
$db->close();
$finalArray=array();
$finalArray[0]=date("Y-m-d H:i:s");
$finalArray[1]=$result;

echo json_encode($finalArray);