import { NextRequest, NextResponse } from "next/server";
import dbPool from "../../../../lib/db";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";

const JWT_SECRET = process.env.JWT_SECRET!;
const JWT_EXPIRES_IN = "1h";

export async function POST(req: NextRequest) {
    /*
     * parse request body
     */
    const { email, password } = await req.json();

    /*
     * validate required fields  
     */
    if (!email || !password) {
        return NextResponse.json({ message: "Missing email/username or password" }, { status: 400 });
    }

    try {
        /*
         * find user by email or username
         */
        const [userRows]: any = await dbPool.query(
            "SELECT * FROM users WHERE email = ? OR username = ?",
            [email, email]
        );

        /*
         * if user not found, return unauthorized
         */
        if (userRows.length === 0) {
            return NextResponse.json({ message: "Invalid credentials" }, { status: 401 });
        }

        const user = userRows[0];

        const now = new Date();
        const lockedUntil = user.locked_until ? new Date(user.locked_until) : null;

        /*
         * check if account is locked 
         */
        if (user.is_locked && lockedUntil && lockedUntil > now) {
            const waitMinutes = Math.ceil((lockedUntil.getTime() - now.getTime()) / 60000);
            return NextResponse.json({
                message: `Too many attempts. Try again in ${waitMinutes} minute(s).`
            }, { status: 403 });
        }

        /*
         * compare password 
         */
        const isPasswordValid = await bcrypt.compare(password, user.password);

        if (!isPasswordValid) {
            let attempts = user.failed_attempts + 1;
            let locked = false;
            let lockUntil: Date | null = null;

            if (attempts >= 5) {
                /*
                 * lock account with exponential backoff 
                 */
                const lockDurationMinutes = Math.pow(2, attempts - 5);
                lockUntil = new Date(now.getTime() + lockDurationMinutes * 60000);
                locked = true;
            }

            /*
             * update failed attempts 
             */
            await dbPool.query(
                "UPDATE users SET failed_attempts = ?, last_failed_at = ?, is_locked = ?, locked_until = ? WHERE id = ?",
                [attempts, now, locked, lockUntil, user.id]
            );

            return NextResponse.json({ message: "Invalid credentials" }, { status: 401 });
        }

        /*
         * reset failed attempts on successful login 
         */
        await dbPool.query(
            "UPDATE users SET failed_attempts = 0, last_failed_at = NULL, is_locked = FALSE, locked_until = NULL WHERE id = ?",
            [user.id]
        );

        /*
         * generate JWT token 
         */
        const token = jwt.sign({
            id: user.id,
            name: user.name,
            username: user.username,
            email: user.email
        }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });

        /*
         * return response with token and user info
         */
        return NextResponse.json({
            message: "Login successful",
            token,
            user: {
                id: user.id,
                name: user.name,
                username: user.username,
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
