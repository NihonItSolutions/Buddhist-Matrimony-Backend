# Buddhist Matrimony Backend

Original REST API backend for a matrimonial platform inspired by common matrimony workflows. It does not copy proprietary source code, branding, text, images, or design.

## Technology Stack

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web, Spring Data JPA, Spring Security
- JWT access and refresh tokens
- Flyway migrations
- MySQL 8 for production, H2 for local dev/test
- Bean Validation, Lombok, manual DTO mapping
- Spring Mail, Actuator
- Springdoc OpenAPI / Swagger UI
- JUnit 5, Mockito, Testcontainers dependencies

Base package: `com.matrimony.backend`

## Suggested UI Color Direction

The backend has no visual UI except Swagger, but for the Angular frontend a Buddhist/Panchashil-inspired palette can work well:

- Primary: deep navy `#0B1F4D`
- Secondary: saffron gold `#F4B400`
- Accent: lotus red `#C62828`
- Support: white `#FFFFFF`, dhamma blue `#2563EB`, soft neutral `#F7F8FA`
- Use deep navy for headers/navigation, saffron for primary actions, red only for warnings/highlight states, and white/neutral backgrounds for readability.

## Prerequisites

- Java 21
- Maven 3.9+
- MySQL 8 for production-like runs

Maven is not currently available on this machine's PATH, so install Maven before running the commands below.

## Database Setup

```sql
CREATE DATABASE buddhist_matrimony CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'matrimony_app'@'%' IDENTIFIED BY 'change_me';
GRANT ALL PRIVILEGES ON buddhist_matrimony.* TO 'matrimony_app'@'%';
FLUSH PRIVILEGES;
```

## Environment

Copy `.env.example` values into your local environment. Important variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `FILE_STORAGE_LOCATION`
- `PAYMENT_WEBHOOK_SECRET`
- `PAYMENT_SIGNATURE_SECRET`
- `DEV_ADMIN_PASSWORD`

Do not commit real secrets.

## Run

Development profile defaults to H2:

```bash
mvn spring-boot:run
```

Production-like MySQL run:

```bash
set SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

Flyway migrations run automatically at startup. Swagger is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Health check:

```text
http://localhost:8080/actuator/health
```

## API Modules

- Authentication: `/api/auth`
- Profiles and onboarding: `/api/profiles`
- Profile photos: `/api/profiles/me/photos`
- Partner preferences: `/api/partner-preferences`
- Search and recommendations: `/api/profiles/search`, `/api/profiles/recommendations`
- Interests: `/api/interests`
- Shortlists: `/api/shortlists`
- Blocks: `/api/blocks`
- Reports: `/api/reports`
- Contact requests: `/api/contact-requests`
- Messaging: `/api/conversations`, `/api/messages`
- Notifications: `/api/notifications`
- Privacy: `/api/privacy-settings`
- Membership: `/api/membership-plans`, `/api/subscriptions`
- Payments: `/api/payments`
- Support and feedback: `/api/support`, `/api/feedback`, `/api/contact-us`
- Success stories: `/api/success-stories`
- Master data: `/api/master`
- Admin: `/api/admin`
- Relationship manager: `/api/relationship-manager`

## Auth Flow

1. Register with `/api/auth/register`.
2. Verify mobile OTP with `/api/auth/mobile-otp/verify`.
3. Login with email or mobile at `/api/auth/login`.
4. Send `Authorization: Bearer <accessToken>` for protected endpoints.
5. Rotate refresh tokens with `/api/auth/token/refresh`.
6. Logout invalidates the current refresh token version.

OTP values are hashed in storage. The dev profile prints OTP/reset values to console for local testing.

## Profile Flow

1. Registration creates a draft profile.
2. Update sections under `/api/profiles/me/*`.
3. Add partner preference.
4. Upload photos. Photos remain pending until admin approval.
5. Check `/api/profiles/me/completion`.
6. Submit with `/api/profiles/me/submit-for-approval`.
7. Admin approves profile and photos.

## Payment Flow

1. Fetch plan from `/api/membership-plans`.
2. Create order with `/api/payments/orders`.
3. Backend calculates amount from the plan record.
4. Verify with `/api/payments/verify`.
5. Subscription activates only after server-side signature verification.
6. Webhooks are idempotent through `payment_webhook_events`.

Mock signature format for development:

```text
sha256(orderId + "|" + paymentId + "|" + PAYMENT_SIGNATURE_SECRET)
```

## File Upload

Local storage is configured with:

```text
FILE_STORAGE_LOCATION=./uploads
MAX_PHOTO_SIZE_BYTES=5242880
```

Allowed image MIME types:

- `image/jpeg`
- `image/png`
- `image/webp`

The storage service is behind `FileStorageService` and can be replaced with S3, Cloudinary, or Supabase Storage.

## Tests

```bash
mvn clean test
mvn clean package
```

Current tests cover:

- Strong password validation
- Profile completion calculation
- Spring Boot context smoke test

## Flyway Migrations

- `V1__create_authentication_tables.sql`
- `V2__create_profile_tables.sql`
- `V3__create_preference_tables.sql`
- `V4__create_interaction_tables.sql`
- `V5__create_messaging_tables.sql`
- `V6__create_membership_payment_tables.sql`
- `V7__create_support_admin_tables.sql`
- `V8__create_master_tables.sql`
- `V9__insert_default_master_data.sql`
- `V10__create_indexes.sql`

## Postman

Collection:

```text
postman/buddhist-matrimony.postman_collection.json
```

Variables:

- `baseUrl`
- `accessToken`
- `refreshToken`
- `adminAccessToken`
- `matrimonyId`
- `profileId`
- `interestId`
- `conversationId`

## Development Admin

Set:

```text
DEV_ADMIN_PASSWORD=StrongAdmin@123
```

Default dev admin:

```text
Email: admin@buddhistmatrimony.local
Mobile: 9999999999
```

The admin is created only in the `dev` profile and only when `DEV_ADMIN_PASSWORD` is present.

## Production Notes

- Replace mock payment gateway with Razorpay integration behind `PaymentGatewayService`.
- Use a real SMS provider behind `OtpSender`.
- Use object storage and signed/private URLs for photographs.
- Use a distributed rate limiter for auth, OTP, and payment endpoints.
- Configure HTTPS, reverse proxy headers, and production CORS origins.
- Review data retention/anonymization policy before launch.
- Expand integration tests with MySQL Testcontainers once Maven and Docker are available.
