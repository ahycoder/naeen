<?php
//TODO timezone??
date_default_timezone_set("Asia/Tehran");
date_default_timezone_set("Europe/London");
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');


updateCurrentCity("32.86","53.08",21);//naeen
updateCurrentCity("33.77","55.08",22);//khor
updateCurrentCity("33.30","53.69",23);//anarak
updateCurrentCity("32.96","52.80",24);//niupdateCurrentCity(

function updateCurrentCity($lat,$lan,$weather_id){
    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lan&appid=0bd1313c936fe3998940d4be57ba7fb7&units=metric",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT => 30,
        CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
        CURLOPT_CUSTOMREQUEST => "GET",
        CURLOPT_HTTPHEADER => array("cache-control: no-cache"),
    ));
    $response = json_decode(curl_exec($curl),true);
    curl_close($curl);
    $result=array();
    $main=$response['weather'][0]['main'];
    $temp=$response['main']['temp'];
    $humidity=$response['main']['humidity'];
    $wind=$response['wind']['speed'];
    $sunset=$response['sys']['sunset']*1000;
    $date=round(microtime(true)*1000);
    $db = new Db();
    updateForeCast($db,$main,$temp,$humidity,$wind,$sunset,$date,$weather_id);
    $db->close();
}
function updateForeCast($db,$main,$temp,$humidity,$wind,$sunset,$date,$weather_id){
    $db->insert("UPDATE weather SET weather_main=:weather_main , weather_temp=:weather_temp , weather_humidity=:weather_humidity,weather_wind=:weather_wind , weather_sunset=:weather_sunset , weather_date=:weather_date WHERE weather_id LIKE :weather_id", array(
        'weather_main' => $main,
        'weather_temp' => $temp,
        'weather_humidity' => $humidity,
        'weather_wind' => $wind,
        'weather_sunset' => $sunset,
        'weather_date' => $date,
        'weather_id' => $weather_id
    ));
}
?>