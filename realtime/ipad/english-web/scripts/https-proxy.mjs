import fs from "node:fs";
import http from "node:http";
import https from "node:https";

function readArg(name) {
  const index = process.argv.indexOf(name);
  return index >= 0 ? process.argv[index + 1] : undefined;
}

const listenPort = Number(readArg("--listen-port") || 53000);
const targetPort = Number(readArg("--target-port") || 53001);
const keyPath = readArg("--key");
const certPath = readArg("--cert");

if (!keyPath || !certPath) {
  console.error("Missing --key or --cert");
  process.exit(1);
}

const server = https.createServer(
  {
    key: fs.readFileSync(keyPath),
    cert: fs.readFileSync(certPath),
  },
  (req, res) => {
    const proxyReq = http.request(
      {
        hostname: "127.0.0.1",
        port: targetPort,
        method: req.method,
        path: req.url,
        headers: {
          ...req.headers,
          host: req.headers.host,
        },
      },
      (proxyRes) => {
        res.writeHead(proxyRes.statusCode || 502, proxyRes.headers);
        proxyRes.pipe(res);
      },
    );

    proxyReq.on("error", (error) => {
      res.writeHead(502, { "content-type": "text/plain; charset=utf-8" });
      res.end(`HTTPS proxy error: ${error.message}`);
    });

    req.pipe(proxyReq);
  },
);

server.on("listening", () => {
  console.log(`HTTPS proxy listening on https://0.0.0.0:${listenPort}`);
  console.log(`Forwarding requests to http://127.0.0.1:${targetPort}`);
});

server.listen(listenPort, "0.0.0.0");

function shutdown() {
  server.close(() => process.exit(0));
}

process.on("SIGINT", shutdown);
process.on("SIGTERM", shutdown);
