<?php
//header('Content-Type: text/html; charset=utf-8');
date_default_timezone_set("Asia/Tehran");
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
require_once(getcwd() . '/town.php');

$Key=$_POST["Key"];

if($Key=="CurrentTime"){
    echo milliseconds();
}
function milliseconds() {
    $mt = explode(' ', microtime());
    return ((int)$mt[1]) * 1000 + ((int)round($mt[0] * 1000));
}

?>