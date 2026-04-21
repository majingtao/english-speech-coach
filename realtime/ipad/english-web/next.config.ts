import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  async rewrites() {
    const apiTarget = process.env.NEXT_PUBLIC_API_BASE_URL || "http://127.0.0.1:48080";
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
