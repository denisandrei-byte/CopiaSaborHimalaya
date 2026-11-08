//Definicion de llamadas al backend para usuarios


export interface Usuario {
    idUsuario: number
    nombre : string
    direccion : string
    login : string
    email : string
    contrasena : string
    activo : boolean
}