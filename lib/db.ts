import mysql from "mysql2/promise";

const dbPool = mysql.createPool({
    host: "localhost",
    user: "root",
    password: "",
    "database": "event_scheduler",
    waitForConnections: true,
    connectionLimit: 10
})

export default dbPool;