export interface LoginDTO {
    username: string;
    password: string;
    loggedInUserId: number | undefined;
}

export interface User {
    id: number 
    username: string 
    firstname: string;
    lastname: string;
    email: string;
    password: string;
    booksRented: Book[]
}

export interface Book {
    id: number
    title: string
    numberOfCopies: number
    authorFirstname: string
    authorLastname: string
    rentByUser: string[]
}

export interface Author {
    id: number
    firstname: string
    lastname: string
}