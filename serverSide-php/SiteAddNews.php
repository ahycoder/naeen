<?php
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
require_once(getcwd() . '/town.php');
date_default_timezone_set("Asia/Tehran");
?>
<html>
<head>
    <title>اضافه کردن خبر</title>
    <meta charset="utf-8" />
    <style>
        body{
            direction:rtl;
            font-family:Tahoma;
        }
        *{
            padding:0;
            margin:0;
        }
        input , textarea ,select , button{
            font-family:inherit;
        }
        body {
            direction: rtl;
            font-family: Tahoma;
        }
        form{
            margin:15px auto;
            width:550px;
            border-radius:5px;
            background-color:#eef;
            box-shadow:0 0 8px;
        }
        fieldset{
            padding:10px;
            margin:10px;
            border:1px solid gray;
        }
        label{
            width:110px;
            font-size:14px;
            display:inline-block;
        }
        label.radio , label.check{
            width:35px;
        }
        legend{
            padding:5px;
        }
        .box{
            box-shadow:3px 3px 5px #ddd inset;
            border-radius:5px;
            border:1px solid gray;
            padding:5px;
        }
        input{
            margin:8px;
            width:180px;
            direction:ltr;
        }
        input[name="fullname"]{
            direction:rtl;
        }
        input[type="radio"] , input[type="checkbox"]{
            width:12px;
            vertical-align:middle;
        }
        select{
            margin:8px;
            width:120px;
        }
        textarea{
            width:280px;
            height:130px;
            margin:8px;
            resize:none;
            vertical-align:middle;
        }
        .button{
            width:70px;
            padding:6px 8px;
            background-image:linear-gradient(#ccf , #aad);
            border:1px solid gray;
            border-radius:4px;
        }
        .button:first-of-type{
            margin-right:200px;
        }
        .button:hover{
            background-image:linear-gradient(#aad , #88b);
        }
    </style>
</head>
<body>
<!-- Forms Markup -->
<form action="" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>اطلاعات کاربری</legend>
        <label>نام کاربری</label><input type="text" name="user" /><br />
        <label>کلمه عبور</label><input type="password" name="pass" /><br />
    </fieldset>
    <fieldset>
        <legend>انتخاب عکس ها</legend>
    <input  accept="image/x-png,image/jpeg" type="file" name="fileToUpload" id="fileToUpload" >
    </fieldset>
    <fieldset>
        <legend>خبر</legend>
        <label>عنوان خبر</label><input type="text" name="Title" /><br />

        <label>الویت</label>
        <select name="news_sort">
            <option value="100">عادی</option>
            <option value="500">بالا</option>
            <option value="1000">بالاترین</option>
        </select><br />
        <label>متن خبر</label>
        <textarea name="description"></textarea>

        <label> نمایش</label><input type="checkbox" name="isShow" />

    </fieldset>
    <input type="submit" value="ارسال" />
    <input type="reset" value="نوسازی" />
</form>
<?php

if(isset($_POST['user'])&& isset($_POST['pass']) ){
    if($_POST['user']=="ahmad" && $_POST['pass']=="226"){
        $title = $_POST['Title'];
        $desc = $_POST['description'];
        if(strlen($title)<5 || strlen($desc)<10){
            echo "طول عنوان و متن کافی نیست";
            return;
        }else{
            if (empty(basename($_FILES['fileToUpload']['name']))) {
                echo "هیچ تصویری انتخاب نشده است";
            }
            else {
                if ($_FILES["fileToUpload"]["size"] > 500000) {
                    echo "حجمم تصویر زیاد است";
                }else{
                    $imageFileType = strtolower(pathinfo( basename($_FILES["fileToUpload"]["name"]),PATHINFO_EXTENSION));
                    if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg") {
                        echo "باید تصویر انتخاب کنید";
                    }else{
                        $date=date("Y_m_d_H_i_s");

                        //TODO change domain
                        $fileName= "image/news/".$date. basename($_FILES['fileToUpload']['name']);
                        if (move_uploaded_file($_FILES['fileToUpload']['tmp_name'], $fileName)) {
                            $news_imgUrl="http://192.168.1.102/firstProject/$fileName";
                                $isShow=0;
                                if(isset($_POST['isShow'])){
                                    $isShow=1;
                                }
                                $news_date= date("Y-m-d H:i:s");
                                  $news_sort=$_POST['news_sort'];
                                $db = new Db();
                                $result = $db->insert("INSERT INTO news (news_title,news_desc,news_imgUrl,news_date,news_sort,news_isShow) VALUES
                                                           (:news_title,:news_desc,:news_imgUrl,:news_date,:news_sort,:news_isShow)", array(
                                    'news_title' => $title,
                                    'news_desc' => $desc,
                                    'news_imgUrl' => $news_imgUrl,
                                    'news_date' => $news_date,
                                    'news_sort' => $news_sort,
                                    'news_isShow' => $isShow
                                ));
                                $db->close();
                                echo $result;
                        }
                        else {
                            echo "<br>خطایی هنگام بارگذاری رخ داده است";
                        }

                    }
                }


            }

        }


    }else{
        ?><script>alert("نام کاربری یا رمز اشتباه است")</script><?php
    }
}
?>
</body>
</html>
