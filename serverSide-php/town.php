<?php
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
class Town {

    function allSelect($tabel){
        $db = new Db();
        $results = $db->query("SELECT * FROM $tabel");
        return $results;
        $db->close();
    }
    function newsOne($news_id){
        $db = new Db();
        $results = $db->query("SELECT * FROM news WHERE news_id LIKE (:news_id)", array(
			'news_id' => $news_id
		));
        return $results;
        $db->close();
    }
    function commentsOneNews($news_id){
        $db = new Db();
        $results = $db->query("SELECT * FROM newsComment WHERE news_id LIKE (:news_id)", array(
			'news_id' => $news_id
		));
        return $results;
        $db->close();
    }

    function registerVerifyCodeInDbMobile($mobile,$verifyCode,$userId,$login_date) {
        $db = new Db();
        $result = $db->insert("INSERT INTO login (login_mobile,login_verifyCode,login_userId,login_date) VALUES
       (:login_mobile,:login_verifyCode,:login_userId,:login_date)", array(
            'login_mobile' => $mobile,
            'login_verifyCode' => $verifyCode,
            'login_userId' => $userId,
            'login_date' => $login_date
        ));
        $db->close();
    }


    function isExist($userId) {
		$db = new Db();
		$result = $db->query("SELECT login_verifyCode,login_date FROM login WHERE login_userId LIKE (:login_userId) ", array(
			'login_userId' => $userId
		));
		$db->close();
        return $result;

	}


	function makeCodeVerify() {
		return rand(11111, 99999);
	}

	function sendSMS($number, $code) {
		if ($firstCharacter = $number[0] == '0') {
			$number = substr($number, 1, 11);
		}

		$url = "37.130.202.188/services.jspd";

		$rcpt_nm = array($number);
		$param = array
		(
			'uname' => 'Mojtabamo64',
			'pass' => 'M16841684',
			'from' => '100020400',
			'message' => "کد عضویت:" . $code,
			'to' => json_encode($rcpt_nm),
			'op' => 'send'
		);

		$handler = curl_init($url);
		curl_setopt($handler, CURLOPT_CUSTOMREQUEST, "POST");
		curl_setopt($handler, CURLOPT_POSTFIELDS, $param);
		curl_setopt($handler, CURLOPT_RETURNTRANSFER, true);
		$response2 = curl_exec($handler);

		// $response2 = json_decode($response2);
		// $res_code = $response2[0];
		// $res_data = $response2[1];
		//echo $res_data;
	}





	function editVerifyCodeInDbMobie($name,$family,$meliCode,$birthDay,$mobile,$imgUserPath,$verifyCode,$verifyDate,$userId){
		$db = new Db();
		$result = $db->insert("UPDATE users SET name=:userName,family=:family,meliCode=:meliCode,birthDay=:birthDay,imgUserPath=:imgUserPath,verifyCode=:verifyCode,verifyDate=:verifyDate,userId=:userId WHERE mobile LIKE :mobile", array(
			'userName' => $name,
			'family' => $family,
			'meliCode' => $meliCode,
			'birthDay' => $birthDay,
			'mobile' => $mobile,
			'imgUserPath' => $imgUserPath,
			'verifyCode' => $verifyCode,
			'verifyDate' => $verifyDate,
			'userId' => $userId
		));
		$db->close();
	}
	function reggisterVerifyCodeInDbMobie($name,$family,$meliCode,$birthDay,$mobile, $imgUserPath,$verifyCode,$verifyDate,$userId) {
		$db = new Db();
			$result = $db->insert("INSERT INTO users (name,family,meliCode,birthDay,mobile,imgUserPath,verifyCode,verifyDate,userId) VALUES
       (:userName, :family ,:meliCode,:birthDay,:mobile,:imgUserPath,:verifyCode,:verifyDate,:userId)", array(
				'userName' => $name,
				'family' => $family,
				'meliCode' => $meliCode,
				'birthDay' => $birthDay,
				'mobile' => $mobile,
				'imgUserPath' => $imgUserPath,
				'verifyCode' => $verifyCode,
				'verifyDate' => $verifyDate,
				'userId' => $userId
			));
			$db->close();

	}


	function checkVerifyCode($mobile, $verifyCodeAndroid) {
		$db = new Db();
		$results = $db->query("SELECT verifyCode,verifyDate FROM users WHERE mobile LIKE :mobile", array(
			'mobile' => $mobile
		));

		foreach ($results as $result) {
			$verifyCode = $result['verifyCode'];
			$verifyDate = $result['verifyDate'];

			$diff = abs(strtotime(date("Y-m-d H:i:s")) - strtotime($verifyDate));
			if ($diff < 5*60*60) { //time to get SMS
				if ($verifyCodeAndroid."" == $verifyCode."") {
					return 100;
				} else {
					return 0;//code err
				}
			} else {
				return 1;//time err
			}
		}
		$db->close();

	}


	function getSlider($tabelId){
		$db = new Db();
		$results = $db->query("SELECT * FROM slider WHERE tabelId LIKE :tabelId", array(
			'tabelId' => $tabelId
		));
		return $results;
		$db->close();
	}
	function listOfCentralWhitOutFilter($tabel,$childId){
		$db = new Db();
		$results = $db->query("SELECT id,name,manager,phone FROM $tabel WHERE childId LIKE :childId", array(
			'tabel' => $tabel,
			'childId' => $childId
		));
		return $results;
		$db->close();
	}
	function listOfCentralDistanceFilter($tabel,$childId,$locationLat,$locationLan){
		$db = new Db();
		$results = $db->query("", array(
			'tabel' => $tabel,
			'childId' => $childId,
			'locationLat' => $locationLat,
			'locationLan' => $locationLan
		));
		return $results;
		$db->close();
	}
	function listOfCentralFavoriteFilter($tabel,$childId){
		$db = new Db();
		$results = $db->query("", array(
			'tabel' => $tabel,
			'childId' => $childId,
		));
		return $results;
		$db->close();
	}
function InfoOneCenter($tabel,$id){
	$db = new Db();
	$results = $db->query("SELECT * FROM $tabel WHERE id LIKE :id", array(
		'tabel' => $tabel,
		'id' => $id
	));
	return $results;
	$db->close();
}
	function insertReport($userId,$phone,$suggestion,$censure,$app,$center,$txtReport,$date,$tabel,$childId)
	{
		$db = new Db();
		$result = $db->insert("INSERT INTO report (userId,phone,suggestion,censure,app,center,txtReport,dateReport,tabel,childId) VALUES
       (:userId, :phone ,:suggestion,:censure,:app,:center,:txtReport,:dateReport,:tabel,:childId)", array(
			'userId' => $userId,
			'phone' => $phone,
			'suggestion' => $suggestion,
			'censure' => $censure,
			'app' => $app,
			'center' => $center,
			'txtReport' => $txtReport,
			'dateReport' => $date,
			'tabel' => $tabel,
			'childId' => $childId));
		$db->close();
		return $result;
	}
	function getAfterLastUpdateTime($tabel ,$labLastTime){
		$db = new Db();
		$result = $db->query("SELECT id,childId,name,manager,locationLat,locationLan,phone,rate,address,lastUpdateTime FROM $tabel WHERE lastUpdateTime > (:lastUpdateTimeApp)  ", array(
			'lastUpdateTimeApp' => $labLastTime
		));
		$db->close();
		return $result;

	}
	function tabelsOption(){
		$db = new Db();
		$results = $db->query("SELECT lastUpdateTime FROM  tabelsoption",array());
		$db->close();
		return $results;
	}


}