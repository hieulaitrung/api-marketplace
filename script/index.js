'use strict';
const fs   = require('fs');
const jwt  = require('jsonwebtoken');

// PAYLOAD
var payload = {
 email: "hieulaitrung@gmail.com",
 scope: "api",
 publishers: "1 5"
};
// PRIVATE and PUBLIC key
var privateKEY  = fs.readFileSync('./private.key', 'utf8');
var publicKEY  = fs.readFileSync('./public.key', 'utf8');
var i  = 'Demo corp';          // Issuer
var s  = '1';        // Subject
var a  = 'App'; // Audience
// SIGNING OPTIONS
var signOptions = {
 issuer:  i,
 subject:  s,
 audience:  a,
 expiresIn:  "1s",
 algorithm:  "RS256"
};


var token = jwt.sign(payload, privateKEY, signOptions);
console.log("Token - " + token)