import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  allowedDevOrigins: ["192.168.0.7"],
  async rewrites() {
    // Keep the upstream address server-only. Browsers on the LAN call the
    // same-origin /app-api and /admin-api paths below instead of their own
    // 127.0.0.1 address.
    const apiTarget = process.env.API_TARGET || "http://127.0.0.1:48080";
    return [
      {
        source: "/app-api/:path*",
        destination: `${apiTarget}/app-api/:path*`,
      },
      {
        source: "/admin-api/:path*",
        destination: `${apiTarget}/admin-api/:path*`,
      },
    ];
  },
  images: {
    remotePatterns: [
      {
        protocol: "http",
        hostname: "127.0.0.1",
        port: "48080",
      },
    ],
  },
};

export default nextConfig;
