# AJUDA AÍ — Trabalho Prático 2 de AEDs III

Sistema de perguntas e respostas em interface textual, desenvolvido em Java para a disciplina de Algoritmos e Estruturas de Dados III (AEDs III). Este relatório documenta o que foi feito no TP2: a listagem paginada das perguntas mais recentes, as respostas às perguntas, a avaliação das respostas e os ajustes na área do usuário, com os relacionamentos N:N mantidos por Árvores B+.

---

## 1. Participantes

| Participante |
|---|
| Lucas José Souza Rodrigues |
| Luca Maciel |
| Matheus Mendes Senna |
| Saulo |

---

## 2. Vídeo de demonstração

**Link do vídeo:**  

**Descrição:**  

---

## 3. Como compilar e executar

O projeto utiliza Java e não exige bibliotecas externas nem ferramenta de build. É necessário ter um JDK instalado.

**Linux / macOS**

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin Principal
```

**Windows (PowerShell)**

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp bin Principal
```

Os arquivos de dados e índices são criados na pasta `dados/`, relativa ao diretório de onde o programa for executado. Preserve essa pasta caso queira manter os dados locais de teste.

---

## 4. O que o sistema faz

O **AJUDA AÍ** é um sistema textual de perguntas e respostas. Depois de entrar no sistema, o usuário pode publicar perguntas, consultar as perguntas de todos os usuários, respondê-las e avaliar as respostas dos outros usuários.

O fluxo implementado no TP2 é:

1. **Perguntas mais recentes** (`Início > Buscar perguntas > Perguntas mais recentes`): as perguntas ativas de todos os usuários aparecem da mais recente para a mais antiga, cinco por página, com o autor e a data. As opções `(A)` e `(B)` voltam e avançam as páginas sem ultrapassar os limites.
2. **Pergunta**: ao escolher o número de uma pergunta, o sistema mostra a pergunta e as suas respostas ativas, ordenadas da maior para a menor nota, com autor, data, nota e quantidade de avaliações. A opção `(A) Responder à pergunta` abre a tela de resposta, que reapresenta a pergunta.
3. **Resposta**: ao escolher o número de uma resposta, o sistema mostra a pergunta e a resposta completa. Se a resposta for do próprio usuário, a única opção é **arquivá-la**. Se for de outro usuário, ele pode **concordar** (`+1`) ou **discordar** (`-1`) da resposta. Cada usuário avalia uma resposta no máximo uma vez.
4. **Minha área**:
   - **Minhas perguntas**: além de incluir, alterar e arquivar perguntas (TP1), ao abrir uma pergunta o autor vê as respostas dos outros usuários e pode **arquivar uma resposta** que considere incompatível com a pergunta ou com as regras do sistema.
   - **Minhas respostas**: lista as respostas ativas escritas pelo usuário. Ao abrir uma, ele vê a pergunta e a resposta e pode arquivá-la.
   - **Minhas avaliações**: lista as respostas que o usuário avaliou. Ao abrir uma, ele vê a pergunta, a resposta e a avaliação feita (positiva ou negativa) e pode **cancelar a avaliação**; a nota e a quantidade de avaliações da resposta são ajustadas. Como não é possível alterar uma avaliação, para mudá-la o usuário cancela a anterior e avalia de novo.

Respostas não são alteradas nem excluídas, apenas arquivadas, porque já podem ter recebido avaliações. Uma resposta arquivada deixa de aparecer nas listagens.

---

## 5. Organização do projeto

```text
TP2_AEDS3/
├── src/
│   ├── Principal.java              ponto de entrada
│   ├── aed3/                       estruturas-base fornecidas para o projeto
│   ├── entidades/                  entidades gravadas em arquivo
│   ├── arquivos/                   CRUDs e índices associados
│   ├── indices/                    pares chave-valor usados nos índices
│   └── menus/                      menus e interface textual
├── dados/                          arquivos de registros e índices
├── docs/img/                       capturas de tela
├── bin/                            bytecode (gerado localmente)
└── README.md                       este relatório
```

### 5.1. Classes criadas pelo grupo

| Classe | Pacote | O que faz |
|---|---|---|
| `Principal` | *(default)* | Ponto de entrada da aplicação. |
| `Usuario` | `entidades` | Entidade que representa um usuário. |
| `Pergunta` | `entidades` | Entidade pergunta, com autor, texto, palavras-chave, datas e status. |
| `Resposta` | `entidades` | Entidade resposta: `idResposta`, `idPergunta`, `idUsuario`, `criacao`, `resposta`, `nota`, `avaliacoes` e `ativa`. É a entidade intermediária do relacionamento N:N entre usuários e perguntas. |
| `Avaliacao` | `entidades` | Entidade avaliação: `idAvaliacao`, `idUsuario`, `idResposta` e `avaliacao` (`true` = positiva, `false` = negativa). |
| `ArquivoUsuario` | `arquivos` | CRUD de usuários e índice de e-mail. |
| `ArquivoPergunta` | `arquivos` | CRUD de perguntas, índice (idUsuario; idPergunta) e índice cronológico usado na listagem das perguntas mais recentes. |
| `ArquivoResposta` | `arquivos` | CRUD de respostas com as duas Árvores B+ do relacionamento: (idPergunta; idResposta) e (idUsuario; idResposta). Lista as respostas ativas de uma pergunta (ordenadas por nota) e de um usuário, e arquiva respostas. |
| `ArquivoAvaliacao` | `arquivos` | CRUD de avaliações com as Árvores B+ (idUsuario; idAvaliacao) e (idResposta; idAvaliacao). Bloqueia a avaliação repetida e ajusta a nota e a quantidade de avaliações da resposta ao criar e ao cancelar uma avaliação. |
| `ParEmailID` | `indices` | Par `(email; idUsuario)` do índice de e-mail. |
| `ParUsuarioPergunta` | `indices` | Par `(idUsuario; idPergunta)` do relacionamento entre usuários e perguntas. |
| `ParDataPergunta` | `indices` | Par `(criacao; idPergunta)` do índice cronológico das perguntas. |
| `ParPerguntaResposta` | `indices` | Par `(idPergunta; idResposta)`: respostas de uma pergunta. |
| `ParUsuarioResposta` | `indices` | Par `(idUsuario; idResposta)`: respostas escritas por um usuário. |
| `ParUsuarioAvaliacao` | `indices` | Par `(idUsuario; idAvaliacao)`: avaliações feitas por um usuário. |
| `ParRespostaAvaliacao` | `indices` | Par `(idResposta; idAvaliacao)`: avaliações recebidas por uma resposta. |
| `MenuAcesso` | `menus` | Tela de acesso: login, cadastro e recuperação de senha. |
| `MenuUsuario` | `menus` | Menu principal, busca das perguntas mais recentes e área do usuário (meus dados, minhas respostas, minhas avaliações). |
| `MenuPerguntas` | `menus` | Minhas perguntas: listagem, inclusão, alteração, arquivamento e respostas recebidas. |
| `MenuResposta` | `menus` | Telas de pergunta, de resposta e de avaliação: responder, concordar, discordar, arquivar resposta e cancelar avaliação. |
| `Console` | `menus` | Entrada de teclado compartilhada, cabeçalho com o caminho (breadcrumb) e leitura das opções. |
| `Formato` | `menus` | Formata datas e a quantidade de avaliações para exibição. |

### 5.2. Classes fornecidas pelo professor (pacote `aed3`)

O projeto utiliza as estruturas-base do pacote `aed3`: o CRUD genérico (`Arquivo`), a Tabela Hash Extensível e a Árvore B+. Na `ArvoreBMais` o grupo corrigiu dois defeitos encontrados durante os testes; eles estão descritos na seção 8.6.

---

## 6. Estrutura dos arquivos e dos índices

Os registros são armazenados em arquivos próprios, utilizando a estrutura do CRUD genérico. Os índices permitem localizar registros e percorrer os relacionamentos.

| Arquivo de dados | Índice direto | Índices indiretos (Árvores B+) |
|---|---|---|
| `dados/usuarios/usuarios.db` | Hash Extensível: `idUsuario` → endereço | Hash Extensível: `email` → `idUsuario` |
| `dados/perguntas/perguntas.db` | Hash Extensível: `idPergunta` → endereço | `(idUsuario; idPergunta)` e `(criacao; idPergunta)` |
| `dados/respostas/respostas.db` | Hash Extensível: `idResposta` → endereço | `(idPergunta; idResposta)` e `(idUsuario; idResposta)` |
| `dados/avaliacoes/avaliacoes.db` | Hash Extensível: `idAvaliacao` → endereço | `(idUsuario; idAvaliacao)` e `(idResposta; idAvaliacao)` |

O relacionamento N:N entre usuários e perguntas é representado pela entidade `Resposta`, que guarda os dois IDs. Na prática ele vira dois relacionamentos 1:N, cada um com a sua Árvore B+: 1 pergunta tem N respostas e 1 usuário escreve N respostas. O mesmo vale para as avaliações: 1 usuário faz N avaliações e 1 resposta recebe N avaliações.

---

## 7. Telas do sistema

As telas abaixo foram copiadas da execução do programa. Depois de `Opção:` aparece o que foi digitado.

### 7.1. Menu principal

```text
AJUDA AÍ 1.0
------------

> Início

(A) Minha área
(B) Buscar perguntas

(S) Sair

Opção: B
```

### 7.2. Perguntas mais recentes

As perguntas ativas aparecem da mais recente para a mais antiga, cinco por página.

```text
AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes

(1) Por que o café parece perder o efeito quando é consumido todos os dias?
    Criada por Ana Marina Reis em 16/10/2026 às 15:49

(2) É seguro comer pão mofado, se você cortar a parte mofada fora?
    Criada por Roberto Souza em 16/10/2026 às 15:49

(3) Por que sentimos mais sono depois do almoço?
    Criada por Roberto Souza em 16/10/2026 às 15:49

(A) Página anterior
(B) Próxima página

(R) Retornar

Opção: 3
```

Nos limites da listagem, o sistema avisa e permanece na mesma página:

```text
Opção: B

Você já está na última página.
```

### 7.3. Pergunta com as respostas ordenadas por nota

```text
AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 16/10/2026 às 15:49

RESPOSTAS
---------
(1) Porque o corpo gasta energia para fazer a digestão.
Respondido por Antônio Carlos Machado em 16/10/2026 às 15:49
Nota: 3  (3 avaliações)

(2) Porque o nosso relógio biológico naturalmente reduz o estado de alerta nesse horário.
Respondido por Ana Cláudia Fagundes em 16/10/2026 às 15:49
Nota: 1  (1 avaliação)

(3) Porque a gente quer enrolar com o trabalho e finge ter sono.
Respondido por você em 16/10/2026 às 15:49
Nota: -2  (2 avaliações)

(A) Responder à pergunta

(R) Retornar

Opção: 1
```

### 7.4. Responder à pergunta

```text
AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Responder

É seguro comer pão mofado, se você cortar a parte mofada fora?
Criada por Roberto Souza em 16/10/2026 às 15:49

Resposta: Depende do tipo de mofo. Pontinhos brancos ou azuis podem ser cortados, mas manchas laranja, amarelas ou pretas indicam que o pão deve ir para o lixo.

Resposta registrada com sucesso!
```

### 7.5. Resposta de outro usuário: concordar ou discordar

```text
AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Resposta

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 16/10/2026 às 15:49

RESPOSTA
--------

Porque o corpo gasta energia para fazer a digestão.
Respondido por Antônio Carlos Machado em 16/10/2026 às 15:49
Nota: 3  (3 avaliações)

(A) Concordar com a resposta
(B) Discordar da resposta

(R) Retornar ao menu anterior

Opção: A
```

Se o usuário já tiver avaliado essa resposta, a segunda avaliação é recusada:

```text
Você já avaliou esta resposta.
Para mudar sua avaliação, cancele a anterior em Minha área > Minhas avaliações.
```

### 7.6. Resposta do próprio usuário: arquivar

```text
AJUDA AÍ 1.0
------------

> Início > Buscar perguntas > Perguntas mais recentes > Pergunta > Resposta

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 16/10/2026 às 15:49

RESPOSTA
--------

Porque a gente quer enrolar com o trabalho e finge ter sono.
Respondido por você em 16/10/2026 às 15:49
Nota: -2  (2 avaliações)

(A) Arquivar resposta

(R) Retornar ao menu anterior

Opção: R
```

### 7.7. Minha área

```text
AJUDA AÍ 1.0
------------

> Início > Minha área

(A) Meus dados
(B) Minhas perguntas
(C) Minhas respostas
(D) Minhas avaliações

(R) Retornar ao menu anterior

Opção: B
```

### 7.8. Minhas perguntas: respostas recebidas e arquivamento de resposta de outro usuário

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas perguntas

(1) 16/10/2026 15:49
    Por que sentimos mais sono depois do almoço?

(2) 16/10/2026 15:49
    É seguro comer pão mofado, se você cortar a parte mofada fora?

(A) Incluir pergunta

(R) Retornar ao menu anterior

Opção: 1
```

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas perguntas > Pergunta

16/10/2026 15:49
Por que sentimos mais sono depois do almoço?
Palavras-chave: sono;almoço;digestão

RESPOSTAS
---------
(1) Porque o corpo gasta energia para fazer a digestão.
Respondido por Antônio Carlos Machado em 16/10/2026 às 15:49
Nota: 2  (2 avaliações)

(2) Porque o nosso relógio biológico naturalmente reduz o estado de alerta nesse horário.
Respondido por Ana Cláudia Fagundes em 16/10/2026 às 15:49
Nota: 1  (1 avaliação)

(3) Porque a gente quer enrolar com o trabalho e finge ter sono.
Respondido por Dick Vigarista em 16/10/2026 às 15:49
Nota: -2  (2 avaliações)

(A) Alterar dados da pergunta
(B) Arquivar pergunta

(R) Retornar

Opção: 3
```

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas perguntas > Pergunta > Resposta

Por que sentimos mais sono depois do almoço?
Criada por você em 16/10/2026 às 15:49

RESPOSTA
--------

Porque a gente quer enrolar com o trabalho e finge ter sono.
Respondido por Dick Vigarista em 16/10/2026 às 15:49
Nota: -2  (2 avaliações)

(A) Arquivar resposta

(R) Retornar ao menu anterior

Opção: A

Confirma o arquivamento desta resposta? (S/N): S

Resposta arquivada com sucesso!
```

Depois do arquivamento, a resposta deixa de aparecer na pergunta.

### 7.9. Minhas respostas

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas respostas

(1) Porque o corpo gasta energia para fazer a digestão.
    Pergunta: Por que sentimos mais sono depois do almoço?
    Nota: 2  (2 avaliações)

(R) Retornar ao menu anterior

Opção: 1
```

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas respostas > Resposta

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 16/10/2026 às 15:49

RESPOSTA
--------

Porque o corpo gasta energia para fazer a digestão.
Respondido por você em 16/10/2026 às 15:49
Nota: 2  (2 avaliações)

(A) Arquivar resposta

(R) Retornar ao menu anterior

Opção: R
```

### 7.10. Minhas avaliações e cancelamento de uma avaliação

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas avaliações

(1) Porque o corpo gasta energia para fazer a digestão.
    Pergunta: Por que sentimos mais sono depois do almoço?
    Sua avaliação: Positiva

(2) Porque o nosso relógio biológico naturalmente reduz o estado de alerta nesse horário.
    Pergunta: Por que sentimos mais sono depois do almoço?
    Sua avaliação: Positiva

(3) Porque a gente quer enrolar com o trabalho e finge ter sono.
    Pergunta: Por que sentimos mais sono depois do almoço?
    Sua avaliação: Negativa

(R) Retornar ao menu anterior

Opção: 1
```

```text
AJUDA AÍ 1.0
------------

> Início > Minha área > Minhas avaliações > Avaliação

Por que sentimos mais sono depois do almoço?
Criada por Roberto Souza em 16/10/2026 às 15:49

RESPOSTA
--------

Porque o corpo gasta energia para fazer a digestão.
Respondido por Antônio Carlos Machado em 16/10/2026 às 15:49
Nota: 3  (3 avaliações)

Sua avaliação: Positiva (você concordou com a resposta)

(A) Cancelar avaliação

(R) Retornar ao menu anterior

Opção: A

Confirma o cancelamento desta avaliação? (S/N): S

Avaliação cancelada com sucesso!
```

Com o cancelamento, a nota da resposta volta de 3 para 2 e a quantidade de avaliações de 3 para 2 (veja a tela 7.9, feita depois).

---

## 8. Operações especiais implementadas

### 8.1. Índice cronológico e paginação das perguntas

A `ArquivoPergunta` mantém uma Árvore B+ com a chave `ParDataPergunta (criacao; idPergunta)`, atualizada a cada pergunta criada. A listagem lê as chaves dessa árvore, que vêm em ordem crescente de data, coloca-as em ordem decrescente, ignora as perguntas arquivadas e devolve cinco perguntas por página (`listarRecentes(pagina, tamanhoPagina)`). O ID desempata perguntas criadas no mesmo milissegundo. Para que perguntas gravadas antes da criação do índice também apareçam, o construtor completa o índice a partir das perguntas existentes.

### 8.2. Relacionamento N:N entre usuários e perguntas (respostas)

A `ArquivoResposta` grava cada resposta e insere dois pares em duas Árvores B+:

- `(idPergunta; idResposta)`, usada por `getRespostasPorPergunta()` para montar a lista de respostas de uma pergunta;
- `(idUsuario; idResposta)`, usada por `getRespostasPorUsuario()` em **Minhas respostas**.

As consultas usam `-1` no segundo campo como curinga: o `compareTo` dos pares considera iguais duas chaves com o mesmo primeiro campo quando um dos segundos campos é `-1`. Assim, `read(new ParUsuarioResposta(idUsuario, -1))` devolve todas as respostas do usuário sem percorrer a árvore inteira. A mesma técnica foi aplicada a `ParUsuarioPergunta` para listar as perguntas do usuário.

`getRespostasPorPergunta()` devolve apenas as respostas ativas, ordenadas da maior para a menor nota.

### 8.3. Avaliação das respostas

A `ArquivoAvaliacao` mantém as Árvores B+ `(idUsuario; idAvaliacao)` e `(idResposta; idAvaliacao)`. Ao criar uma avaliação:

1. procura, pelas avaliações do usuário, se ele já avaliou aquela resposta; nesse caso lança uma exceção, que a interface mostra como mensagem;
2. grava a avaliação e insere os pares nas duas árvores;
3. na própria `Resposta`, soma 1 à nota (concordou) ou subtrai 1 (discordou) e incrementa a quantidade de avaliações.

A interface só oferece as opções de concordar e discordar quando a resposta é de outro usuário, então ninguém avalia a própria resposta.

### 8.4. Cancelamento de uma avaliação

`ArquivoAvaliacao.delete()` desfaz o efeito da avaliação na resposta (subtrai 1 da nota se ela era positiva, soma 1 se era negativa, e decrementa a quantidade de avaliações), remove os pares das duas árvores e exclui o registro. Depois disso o usuário pode avaliar a resposta de novo.

### 8.5. Arquivamento de respostas e integridade dos relacionamentos

`ArquivoResposta.arquivar()` marca a resposta como inativa (`ativa = false`). Ela continua nos índices, porque as avaliações já feitas continuam apontando para ela, mas deixa de aparecer nas listagens. O arquivamento pode ser feito pelo autor da resposta (na pergunta ou em Minhas respostas) e pelo autor da pergunta (em Minhas perguntas). Uma pergunta arquivada não recebe novas respostas.

As respostas não são excluídas pelo sistema. Mesmo assim, `ArquivoResposta.delete()` foi sobrescrito para remover a resposta das duas árvores, de modo que nenhuma operação do CRUD deixe pares órfãos.

### 8.6. Correções na Árvore B+

Os testes com centenas de inserções e remoções revelaram dois defeitos na `ArvoreBMais` (pacote `aed3`), corrigidos pelo grupo e comentados no código:

1. **Divisão de página interna** (`create1`): quando o elemento vindo de baixo era inserido na última posição da página da esquerda, ele próprio era promovido, mas o ponteiro para a página à sua direita era descartado. A página nova ficava apontando para o filho errado, e as folhas saíam de ordem; buscas exatas chegavam a falhar a partir de umas 20 inserções. Agora o filho à direita do elemento promovido passa a ser o primeiro filho da página nova.
2. **Empréstimo do irmão esquerdo em página interna** (`delete1`): o pai recebia de volta o próprio elemento e o irmão perdia um ponteiro sem perder o elemento, o que gerava `IndexOutOfBoundsException` ao gravar a página. Agora o elemento do pai desce para a página que ficou pequena e o maior elemento do irmão sobe para o pai.

### 8.7. Persistência dos registros e índices

Os dados e índices ficam em arquivos na pasta `dados/`. Depois de encerrar e iniciar a aplicação, os registros continuam disponíveis.

---

## 9. Checklist

> **Há uma listagem paginada de perguntas, que mostra as perguntas em ordem decrescente de data a partir de uma Árvore B+ acrescentada ao CRUD de Perguntas?**

**Sim.** A `ArquivoPergunta` tem a Árvore B+ `(criacao; idPergunta)`, cujas chaves são usadas em ordem decrescente de data; a tela mostra cinco perguntas por página (seção 8.1).

> **Há uma listagem das respostas a uma pergunta selecionada, em ordem decrescente de nota?**

**Sim.** As respostas ativas vêm da Árvore B+ `(idPergunta; idResposta)` e são ordenadas pela nota, da maior para a menor (tela 7.3).

> **O usuário pode responder a uma pergunta?**

**Sim.** Pela opção `(A) Responder à pergunta`, na tela da pergunta (tela 7.4).

> **O usuário pode arquivar a própria resposta?**

**Sim.** Ao abrir a própria resposta, na pergunta ou em Minhas respostas, a única opção é arquivá-la (telas 7.6 e 7.9).

> **O usuário pode avaliar uma resposta?**

**Sim.** Ao abrir a resposta de outro usuário, ele pode concordar ou discordar; a nota e a quantidade de avaliações são ajustadas e a avaliação repetida é recusada (tela 7.5).

> **Na área do usuário, o usuário pode visualizar as respostas às suas perguntas?**

**Sim.** Em Minhas perguntas, ao abrir uma pergunta, as respostas recebidas aparecem ordenadas por nota (tela 7.8).

> **Na área do usuário, o usuário pode arquivar uma resposta de outro usuário a uma das suas perguntas?**

**Sim.** Ao abrir uma resposta a partir de Minhas perguntas, o autor da pergunta pode arquivá-la (tela 7.8).

> **Na área do usuário, o usuário pode visualizar e arquivar as respostas que escreveu?**

**Sim.** Em Minhas respostas, listadas pela Árvore B+ `(idUsuario; idResposta)` (tela 7.9).

> **Na área do usuário, o usuário pode visualizar e arquivar a avaliação que realizou em uma resposta?**

**Sim.** Em Minhas avaliações, listadas pela Árvore B+ `(idUsuario; idAvaliacao)`, o usuário vê a pergunta, a resposta e a avaliação feita e pode cancelá-la (tela 7.10).

> **A integridade do relacionamento entre perguntas e respostas está mantida em todas as operações?**

**Sim.** Toda resposta criada entra nas duas árvores (`(idPergunta; idResposta)` e `(idUsuario; idResposta)`); o arquivamento não remove a resposta dos índices, apenas a esconde das listagens; e a exclusão pelo CRUD retira a resposta das duas árvores (seção 8.5).

> **A integridade do relacionamento entre respostas e avaliações está mantida em todas as operações?**

**Sim.** A criação e o cancelamento de uma avaliação atualizam as duas árvores e ajustam a nota e a quantidade de avaliações da resposta (seções 8.3 e 8.4). Um teste com 700 tentativas de avaliação e o cancelamento de metade delas conferiu, ao final, a nota e a quantidade de avaliações de cada resposta com as avaliações que restaram.

> **O trabalho compila corretamente?**

**Sim.**

> **O trabalho está completo e funcionando sem erros de execução?**

**Sim.** Todas as operações pedidas no enunciado foram implementadas, com exceção da busca por palavras-chave, que o próprio enunciado deixa para depois.

> **O trabalho é original e não a cópia de um trabalho de outro grupo?**

**Sim.**

### 9.1. Resumo dos testes realizados

| Teste | Resultado observado |
|---|---|
| Listagem com cinco perguntas por página, da mais recente para a mais antiga | Funcionou |
| Navegação entre páginas e avisos nos limites da listagem | Funcionou |
| Resposta a uma pergunta e exibição na lista de respostas | Funcionou |
| Ordenação das respostas por nota decrescente | Funcionou |
| Concordar e discordar, com atualização da nota e da quantidade de avaliações | Funcionou |
| Bloqueio da segunda avaliação do mesmo usuário na mesma resposta | Funcionou |
| Arquivamento da própria resposta (pergunta e Minhas respostas) | Funcionou |
| Arquivamento de resposta de outro usuário pelo autor da pergunta | Funcionou |
| Cancelamento de avaliação, com a nota da resposta restaurada | Funcionou |
| Respostas arquivadas fora de todas as listagens | Funcionou |
| Árvores B+ com centenas de inserções e remoções intercaladas (ordem das folhas e buscas) | Funcionou após as correções da seção 8.6 |
| Persistência após reiniciar a aplicação | Funcionou |
