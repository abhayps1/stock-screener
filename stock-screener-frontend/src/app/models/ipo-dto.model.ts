export interface IpoDTO {
  companyShortName: string;
  issueSize: string;
  ipoOpenDate: string | null;     // LocalDate => ISO string (e.g. '2025-06-26')
  ipoClosedDate: string | null;   // LocalDate => ISO string
  ipoCategory: string;
  isOpen: boolean;                // boolean
}
