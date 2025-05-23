import { NextRequest, NextResponse } from "next/server";
import dbPool from "../../../../lib/db";
import bcrypt from "bcryptjs";

export async function POST(req: NextRequest) {
    /*
     * parse request body
     */
    const { name, username, email, password } = await req.json();

    /*
     * validate required fields  
     */
    if (!name || !username || !email || !password) {
        return NextResponse.json({ message: "Missing required field" }, { status: 400 });
    }

    /*
     * validate password complexity
     */
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
    if (!passwordRegex.test(password)) {
        return NextResponse.json(
            { message: "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character." },
            { status: 400 }
        );
    }

    try {
        /*
         * check if a user already exists with the same email or username 
         */
        const [existingEmail]: any = await dbPool.query(
            "SELECT id FROM users WHERE email = ?",
            [email]
        );
        const [existingUsername]: any = await dbPool.query(
            "SELECT id FROM users WHERE username = ?",
            [username]
        );

        /*
         * if user exists, return conflict error 
         */
        if (existingEmail.length > 0) {
            return NextResponse.json({ message: "User with this email already exists" }, { status: 409 });
        }
        if (existingUsername.length > 0) {
            return NextResponse.json({ message: "User with this username already exists" }, { status: 409 });
        }

        /*
         * hash the password securely before storing it
         */
        const hashedPassword = await bcrypt.hash(password, 10);

        /*
         * insert the new user into the database
         */
        await dbPool.query(
            "INSERT INTO users (name, username, email, password) VALUES (?, ?, ?, ?)",
            [name, username, email, hashedPassword]
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
