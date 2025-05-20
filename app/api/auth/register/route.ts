import { NextRequest, NextResponse } from "next/server";
import dbPool from "../../../../lib/db";
import bcrypt from "bcryptjs";

export async function POST(req: NextRequest) {
    /*
     * parse request body
     */
    const { name, email, password } = await req.json();

    /*
     * validate required fields  
     */
    if (!name || !email || !password) {
        return NextResponse.json({ message: "Missing required field" }, { status: 400 });
    }

    try {
        /*
         * check if a user already exists with the same email 
         */
        const [existingUser]: any = await dbPool.query(
            "SELECT id FROM users WHERE email = ?",
            [email]
        );

        /*
         * if user exists, return conflict error 
         */
        if (existingUser.length > 0) {
            return NextResponse.json({ message: "User already exists" }, { status: 409 });
        }

        /*
         * hash the password securely before storing it
         */
        const hashedPassword = await bcrypt.hash(password, 10);

        /*
         * insert the new user into the database 
         */
        await dbPool.query(
            "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
            [name, email, hashedPassword]
        );
        return NextResponse.json({ message: "User created successfully" }, { status: 201 });

    } catch (error) {
        /*
         * log the error and return a generic server error message
         */
        console.error("Registration error:", error);
        return NextResponse.json({ message: "Internal server error" }, { status: 500 });
    }
}
