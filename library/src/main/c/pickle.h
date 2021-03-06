#ifndef QUICKJS_ANDROID_PICKLE_H
#define QUICKJS_ANDROID_PICKLE_H

#include <quickjs.h>
#include <stdbool.h>

#include "bit-source.h"
#include "bit-sink.h"

bool pickle(
        JSContext *ctx,
        JSValue val,
        BitSource *source,
        BitSink *sink,
        int *error_type,
        char *error_msg,
        size_t error_msg_size
);

#endif //QUICKJS_ANDROID_PICKLE_H
