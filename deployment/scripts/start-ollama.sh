mkdir -p ~/.ollama-models


 podman run -it --rm \
    -v ~/.ollama-models:/root/.ollama \
    -p 11434:11434 \
    --name ollama \
    ollama/ollama

podman run -it --rm \
    -v ~/.ollama-models:/root/.ollama \
  -e OLLAMA_CONTEXT_LENGTH=4096 \
  -e OLLAMA_NUM_PARALLEL=1 \
  -e OLLAMA_MAX_LOADED_MODELS=1 \
  -e OLLAMA_KEEP_ALIVE=5m \
  -p 11434:11434 \
  --name ollama ollama/ollama

#docker run -it --rm \
#  -v ~/.ollama-models:/root/.ollama \
#  -p 11434:11434 \
#  --name ollama \
#  ollama/ollama