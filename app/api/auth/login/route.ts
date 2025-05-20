import { NextRequest, NextResponse } from "next/server";
import dbPool from "../../../../lib/db";
import bcrypt from "bcryptjs";

export async function POST(req: NextRequest) {
    /*
     * parse request body
     */
    const { email, password } = await req.json();

    /*
     * validate required fields
     */
    if (!email || !password) {
        return NextResponse.json({ message: "Missing email or password" }, { status: 400 });
    }

    try {
        /*
         * retrieve the user with the given email
         */
        const [userRows]: any = await dbPool.query(
            "SELECT * FROM users WHERE email = ?",
            [email]
        );


        /*
         * if no user found, return unauthorized
         */
        if (userRows.length === 0) {
            return NextResponse.json({ message: "Invalid email or password" }, { status: 401 });
        }

        const user = userRows[0];

        /*
         * compare the provided password with the stored hashed password
         */
        const isPasswordValid = await bcrypt.compare(password, user.password);

        if (!isPasswordValid) {
            return NextResponse.json({ message: "Invalid email or password" }, { status: 401 });
        }

        /*
         * if valid, return success (you can generate and return a JWT here later)
         */
        return NextResponse.json({
            message: "Login successful",
            user: {
                id: user.id,
                name: user.name,
                email: user.email
            }
        }, { status: 200 });

    } catch (error) {
        /*
         * log the error and return a generic server error message
         */
        console.error("Login error:", error);
        return NextResponse.json({ message: "Internal server error" }, { status: 500 });
    }
}
