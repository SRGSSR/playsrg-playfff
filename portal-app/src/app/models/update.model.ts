export class Update {
  [key: string]: string | boolean;
  id!: string;
  packageName!: string;
  version!: string;
  text!: string;
  mandatory!: boolean;
}
