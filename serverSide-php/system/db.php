<?php
class Db {
	private $connection;
	//public static $db;

	public function __construct($option = null) {


		if ($option != null) {
			$host = $option['host'];
			$user = $option['user'];
			$pass = $option['pass'];
			$name = $option['name'];
		} else {
			global $config;
			$host = $config['db']['host'];
			$user = $config['db']['user'];
			$pass = $config['db']['pass'];
			$name = $config['db']['name'];
		}


		$this->connection = new mysqli($host, $user, $pass, $name);
		if ($this->connection->connect_error) {
			echo "Connection failed: " . $this->connection->connect_error;
			exit;
		}

		$this->connection->query("SET NAMES 'utf8'");
	}

    public function safeQuery(&$sql, $data) {
		foreach ($data as $key => $value) {
			$value = $this->connection->real_escape_string($value);
			$value = "'$value'";

			$sql = str_replace(":$key", $value, $sql);
		}

		return $this->connection->query($sql);
	}

	public function first($sql, $data = array()) {
		$records = $this->query($sql, $data);
		if ($records == null) {
			return null;
		}

		return $records[0];
	}

	public function modify($sql, $data = array()) {
		$result = $this->safeQuery($sql, $data);
		if (!$result) {
			echo "Query: " . $sql . " failed due to " . mysqli_error($this->connection);
			exit;
		}
		return $result;
	}

	public function insert($sql, $data = array()) {
		$result = $this->safeQuery($sql, $data);
		if (!$result) {
			echo "Query: " . $sql . " failed due to " . mysqli_error($this->connection);
			exit;
		}
		return $result;
	}

	public function query($sql, $data = array()) {
		$result = $this->safeQuery($sql, $data);
		if (!$result) {
			echo "Query: " . $sql . " failed due to " . mysqli_error($this->connection);
			exit;
		}

		$records = array();

		if ($result->num_rows == 0) {
			return null;
		}

		while ($row = $result->fetch_assoc()) {
			$records[] = $row;
		}

		return $records;
	}

	private function connection() {
		return $this->connection;
	}

	public function close() {
		$this->connection->close();
	}
}
?>