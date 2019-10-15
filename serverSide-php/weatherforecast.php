<?php
date_default_timezone_set("Europe/London");
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');


updateTableWeatherForecat("32.86","53.08",1);//naeen
updateTableWeatherForecat("33.77","55.08",6);//khor
updateTableWeatherForecat("33.30","53.69",11);//anarak
updateTableWeatherForecat("32.96","52.80",16);//nistanak


function updateTableWeatherForecat($lat,$lan,$startRecordDay1City){
    $curl = curl_init();
    curl_setopt_array($curl, array(
        CURLOPT_URL => "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lan&appid=0bd1313c936fe3998940d4be57ba7fb7&units=metric",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT => 30,
        CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
        CURLOPT_CUSTOMREQUEST => "GET",
        CURLOPT_HTTPHEADER => array("cache-control: no-cache"),
    ));
    $response = json_decode(curl_exec($curl),true);
    $result=array();
    $result=$response['list'];
    curl_close($curl);

    $hourFirstId=date("H", strtotime($result[0]['dt_txt']));
    $idStart=0;
    switch ($hourFirstId){
        case 0: $idStart=8;break;
        case 3: $idStart=7;break;
        case 6: $idStart=6;break;
        case 9: $idStart=5;break;
        case 12: $idStart=4;break;
        case 15: $idStart=3;break;
        case 18: $idStart=2;break;
        case 21: $idStart=1;break;
    }
    $tempMinDay1=array();$tempMinDay2=array();$tempMinDay3=array();$tempMinDay4=array();$tempMinDay5=array();
    $tempMaxDay1=array();$tempMaxDay2=array();$tempMaxDay3=array();$tempMaxDay4=array();$tempMaxDay5=array();
    $mainDay1=array();$mainDay2=array();$mainDay3=array();$mainDay4=array();$mainDay5=array();

    for ($i=$idStart;$i<$idStart+8;$i++){
        $tempMinDay1[]= $result[$i]['main']['temp_min'];
        $tempMaxDay1[]= $result[$i]['main']['temp_max'];
        $mainDay1[]= $result[$i]['weather'][0]['main'];
    }
    for ($i=$idStart+8;$i<$idStart+16;$i++){
        $tempMinDay2[]= $result[$i]['main']['temp_min'];
        $tempMaxDay2[]= $result[$i]['main']['temp_max'];
        $mainDay2[]= $result[$i]['weather'][0]['main'];
    }
    for ($i=$idStart+16;$i<$idStart+24;$i++){
        $tempMinDay3[]= $result[$i]['main']['temp_min'];
        $tempMaxDay3[]= $result[$i]['main']['temp_max'];
        $mainDay3[]= $result[$i]['weather'][0]['main'];
    }
    for ($i=$idStart+24;$i<$idStart+32;$i++){
        $tempMinDay4[]= $result[$i]['main']['temp_min'];
        $tempMaxDay4[]= $result[$i]['main']['temp_max'];
        $mainDay4[]= $result[$i]['weather'][0]['main'];
    }
    for ($i=$idStart+32;$i<$idStart+40;$i++){
        if(count($result)>$i){
            $tempMinDay5[]= $result[$i]['main']['temp_min'];
            $tempMaxDay5[]= $result[$i]['main']['temp_max'];
            $mainDay5[]= $result[$i]['weather'][0]['main'];
        }
    }

    $dateDay1=date("Y-m-d H:i:s", strtotime($result[$idStart]['dt_txt']));
    $dateDay2=date("Y-m-d H:i:s", strtotime($result[$idStart+8]['dt_txt']));
    $dateDay3=date("Y-m-d H:i:s", strtotime($result[$idStart+16]['dt_txt']));
    $dateDay4=date("Y-m-d H:i:s", strtotime($result[$idStart+24]['dt_txt']));
    $dateDay5=date("Y-m-d H:i:s", strtotime($result[$idStart+32]['dt_txt']));

    $db = new Db();
    updateForeCast($db,(int)min($tempMinDay1),(int)max($tempMaxDay1),getMain($mainDay1),$startRecordDay1City,$dateDay1);
    updateForeCast($db,(int)min($tempMinDay2),(int)max($tempMaxDay2),getMain($mainDay2),$startRecordDay1City+1,$dateDay2);
    updateForeCast($db,(int)min($tempMinDay3),(int)max($tempMaxDay3),getMain($mainDay3),$startRecordDay1City+2,$dateDay3);
    updateForeCast($db,(int)min($tempMinDay4),(int)max($tempMaxDay4),getMain($mainDay4),$startRecordDay1City+3,$dateDay4);
    updateForeCast($db,(int)min($tempMinDay5),(int)max($tempMaxDay5),getMain($mainDay5),$startRecordDay1City+4,$dateDay5);
    $db->close();
}


function updateForeCast($db,$averageTempMinDay,$averageTempMaxDay,$averageMainDay,$recordId,$dateDay){
    $db->insert("UPDATE weather SET weather_tempMin=:weather_tempMin , weather_tempMax=:weather_tempMax , weather_main=:weather_main ,weather_date=:weather_date WHERE weather_id LIKE :weather_id", array(
        'weather_tempMin' => $averageTempMinDay,
        'weather_tempMax' => $averageTempMaxDay,
        'weather_main' => $averageMainDay,
        'weather_id' => $recordId,
        'weather_date' => $dateDay
    ));
}

function getMain($array){
    $result="";
    if (in_array("Snow",$array)) {
        $result= "Snow";
    }else{
        if (in_array("Rain",$array)) {
            $result= "Rain";
        }else{
            if (in_array("Clouds",$array)) {
                $result= "Clouds";
            }else{
                if (in_array("Clear",$array)) {
                    $result= "Clear";
                }else{

                }
            }
        }
    }
    return $result;
}