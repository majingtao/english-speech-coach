import type { Metadata, Viewport } from "next";
import localFont from "next/font/local";
import "./globals.css";

// 字体文件随项目打包（来自 @fontsource-variable，OFL-1.1），
// 构建时不再访问 fonts.googleapis.com，内网/离线环境也能 next build。
const dmSans = localFont({
  src: "./fonts/DMSans-Variable-latin.woff2",
  variable: "--font-body",
  weight: "100 1000",
  style: "normal",
  display: "swap",
  fallback: ["ui-sans-serif", "system-ui", "sans-serif"],
});

const spaceGrotesk = localFont({
  src: "./fonts/SpaceGrotesk-Variable-latin.woff2",
  variable: "--font-heading",
  weight: "300 700",
  style: "normal",
  display: "swap",
  fallback: ["ui-sans-serif", "system-ui", "sans-serif"],
});

export const metadata: Metadata = {
  title: "English Speech Coach",
  description: "iPad and mobile friendly English speaking coach",
};

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  // Pinch-zoom on the exam/practice screens (two-finger taps during mic use,
  // kids tapping with a stray second finger) shifts the whole fixed layout,
  // pushing the send button off-screen. This is an app-shell UI, not a
  // document meant to be zoomed, so disable manual pinch/double-tap zoom
  // everywhere rather than special-casing individual pages.
  maximumScale: 1,
  userScalable: false,
  viewportFit: "cover",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="en"
      suppressHydrationWarning
      className={`${dmSans.variable} ${spaceGrotesk.variable} h-full antialiased`}
    >
      <body suppressHydrationWarning className="min-h-full flex flex-col">
        {children}
      </body>
    </html>
  );
}
