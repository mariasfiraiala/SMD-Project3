function strip_path(tag, timestamp, record)
    local f = record["filename"]
    if f then
        record["filename"] = f:match("([^/]+)%.log$") or f
    end
    return 1, timestamp, record
end
