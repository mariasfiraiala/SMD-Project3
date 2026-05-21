FROM golang:1.26-alpine AS builder

ARG TARGETOS
ARG TARGETARCH

WORKDIR /app
COPY . .
ENV CGO_ENABLED=0
ENV GOOS=${TARGETOS:-linux}
ENV GOARCH=${TARGETARCH:-amd64}
RUN go get . && go build -o server ./main.go

FROM scratch

COPY --from=builder /app/server /server
COPY ./certs/ /

ENTRYPOINT ["/server"]
CMD ["--help"]
