express = require('express')
mongo   = require('mongodb')

########################################
# Setup
########################################

db = new mongo.Db('mydb', new mongo.Server('localhost', 27017, {}), {})

app = express.createServer(
    express.logger(),
    # Enable JSON parsing
    express.bodyParser(),
    express.errorHandler({ showStack: true, dumpExceptions: true }),
)
########################################
# REST Methods
########################################

# Hello world
app.get('/', (request, response) ->
  response.send('Hello World!')
)

# API Calls

app.all('/ponies', (req, res, next) ->
    res.header("Access-Control-Allow-Origin", "*")
    res.header("Access-Control-Allow-Headers", "X-Requested-With")
    next()
)

###
GET all ponies
###
app.get('/ponies', (request, response) ->
  resultSet = []
  # Query MongoDB.
  db.open( ->
    # Callback when the open call returns.
    console.log("db opened")
    db.collection('testCollection', (err, collection) ->
      # Node calls this when the collection request returns.
      collection.find().toArray((err, results) ->
        if (err)
          console.log("ERROR on retrieve: " + err)
          response.send(500)
        else
          #Node calls this when our insert returns.
          console.log("doc retrieved")
          response.send(
            'ponies' : results
          )
      )
    )
  )
)

# GET one pony
app.get('/ponies/:id', (request, response) ->
    # fetch the proper pony from the DB

    ponyId = new db.bson_serializer.ObjectID.createFromHexString(request.params.id)

    db.open( ->
        console.log("db opened")
        db.collection('testCollection', (err, collection) ->
          # Node calls this when the collection request returns
          collection.findOne({_id: ponyId}, (err, results) ->
         #collection.findOne({_id:'4e8de6233576f6800c000001'}, (err, results) ->
            if (err)
              console.log("ERROR on retrieve: " + err)
              response.send(500)
            else
              #Node calls this when our insert returns.
              console.log("doc retrieved")
              response.send(
                'ponies' : results
              )
          )
        )
    )

    # spit it back
    #req.params.id
    #response.send('pony ' + request.params.id)
)

# PUT pony
app.put('/ponies/:id', (request, response) ->
    # fetch the proper pony from the DB
    ponyId = new db.bson_serializer.ObjectID.createFromHexString(request.params.id)
    newPony = request.body
    console.log("newPony: " + newPony)
    # TODO: Validate newPony's contents

    db.open( ->
        console.log("db opened")
        db.collection('testCollection', (err, collection) ->
            # Node calls this when the collection request returns
            collection.update({_id:ponyId}, {$set: newPony}, {safe:true}, (err, doc) ->

                # Node calls this when the save request returns
                if (err)
                  console.log("ERROR on PUT: " + err)
                  response.send({
                      'error': 1
                      'description': 'Could not PUT pony.'
                  })
                 else
                  console.log("pony updated")
                  response.send({
                      'error': 0
                      'description': 'PUT pony successfully!'
                      'pony': doc
                  })
            )
        )
    )
)

###
POST new pony
###
app.post('/ponies', (request, response) ->
  console.log("new pony posted")
  body = request.body

  # Ensure that we have a valid location
  if !ensureValidPony(body)
    response.send(400)
  else
    # Parse request payload
    doc = body
    # Save payload to MongoDB
    db.open( ->
      # Callback when the open call returns
      console.log("db opened")
      db.collection('testCollection', (err, collection) ->
        # Node calls this when the collection request returns
        collection.insert(doc, (err) ->
          if (err)
            console.log("ERROR on insert: " + err)
           else
            #Node calls this when our insert returns
            console.log("doc inserted")
        )
      )
    )
    response.send(200)
)

app.post('/echo', (request, response) ->
    body = request.body
    console.log('body: ' + body)
    if (body)
        response.send(
            body
        )
    else
        response.send({
            error: 'No body!'
        })
)

# Fire up the app
port = process.env.PORT || 3000
app.listen(port, () ->
    console.log("Listening on " + port)
)
